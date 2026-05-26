# Plan: Corrección de 316 Tests Fallidos tras Migración a Spring Boot 4

## Contexto

El proyecto migró de Spring Boot 3 a Spring Boot 4. Los tests fallaban por dos causas raíces
independientes descubiertas ejecutando `mvn clean test` y analizando los jars de Spring Boot 4:

- **316 de 707 tests** fallan (service tests pasan, solo resource y repository fallan)
- Las fallas son 100% de contexto de carga (ApplicationContext), no de lógica de negocio

---

## Problema 1 — @WebMvcTest (54 clases, ~200 tests)

**Error:**
```
Error creating bean 'jwtSecurityFilterChain': No qualifying bean of type 'HttpSecurity' available
```

**Causa raíz confirmada** (análisis del jar `spring-boot-security-oauth2-resource-server-4.0.0.jar`):

Spring Boot 4 añadió `OAuth2ResourceServerAutoConfiguration` a todos los `@WebMvcTest` via
`META-INF/spring/org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest.imports`.
Esta auto-config intenta crear `jwtSecurityFilterChain(HttpSecurity http)`.

Cadena de fallos:
1. `@WebMvcTest` incluye `SecurityConfig` (tiene `@EnableWebSecurity`)
2. `SecurityConfig` necesita `DelegatedAuthenticationEntryPoint` (un `@Component` no disponible en slice web)
3. `SecurityConfig` falla → no hay `SecurityFilterChain`
4. `@ConditionalOnMissingBean(SecurityFilterChain)` → true → `OAuth2SecurityFilterChainConfiguration` intenta crear `jwtSecurityFilterChain`
5. `HttpSecurity` no disponible (porque `@EnableWebSecurity` no terminó de inicializarse) → falla

---

## Problema 2 — @DataJpaTest (50 clases, ~100 tests)

**Errores:**
```
set [*]client_min_messages = WARNING  →  H2 no soporta este comando PostgreSQL
Schema "TRIALS" no encontrado
Schema "ACUERDOS" no encontrado
```

**Causa raíz confirmada** (análisis de output `mvn test`):

`PrimaryDbConfig` (en `mx.gob.pjpuebla.config`) es **excluido** por el type filter de `@DataJpaTest`.
Spring Boot auto-configura JPA con H2 embebido. Sin embargo, `application.yaml` de producción
fuerza `spring.jpa.database-platform: org.hibernate.dialect.PostgreSQLDialect`, lo que hace
que Hibernate 7 envíe `set client_min_messages = WARNING` a H2 → falla.
Además, entidades del paquete `mx.gob.pjpuebla.trials` usan `@Table(schema = "trials")` y
algunas referencian schema `acuerdos`, pero H2 no tiene esos schemas → falla DDL.

---

## Solución — 4 archivos nuevos, sin modificar archivos existentes

### Archivo 1: `src/test/java/mx/gob/pjpuebla/trials/config/TestWebSecurityConfig.java`

```java
package mx.gob.pjpuebla.trials.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestWebSecurityConfig {

    @Bean
    public DelegatedAuthenticationEntryPoint delegatedAuthenticationEntryPoint() {
        return mock(DelegatedAuthenticationEntryPoint.class);
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return mock(JwtDecoder.class);
    }
}
```

**Por qué funciona:**
- Mock de `DelegatedAuthenticationEntryPoint` satisface `SecurityConfig` → `securityFilterChain` se crea
- Mock de `JwtDecoder` evita que `.jwt(withDefaults())` contacte Keycloak al startup
- Con `SecurityFilterChain` presente → `@ConditionalOnMissingBean` = false → `jwtSecurityFilterChain` NO se crea
- `@AutoConfigureMockMvc(addFilters = false)` ya está en todos los tests → ningún filtro de seguridad ejecuta

### Archivo 2: `src/test/resources/META-INF/spring/org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureWebMvc.imports`

```
mx.gob.pjpuebla.trials.config.TestWebSecurityConfig
```

Spring Boot 4 acumula (no reemplaza) todos los archivos `.imports` del classpath. Este archivo
se suma a los entries del jar para todos los `@WebMvcTest`, sin requerir cambios en los 54 test files.

### Archivo 3: `src/test/resources/application.yaml`

```yaml
spring:
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
```

Override de `org.hibernate.dialect.PostgreSQLDialect` del `application.yaml` principal.
Con H2Dialect, Hibernate no envía `set client_min_messages = WARNING`. Se aplica solo en tests
(test classpath tiene mayor prioridad). No afecta `@WebMvcTest` (no usa JPA).

### Archivo 4: `src/test/resources/schema.sql`

```sql
CREATE SCHEMA IF NOT EXISTS trials;
CREATE SCHEMA IF NOT EXISTS acuerdos;
```

Spring Boot ejecuta `schema.sql` **antes** de que Hibernate genere DDL (orden garantizado por
`@AutoConfigureAfter` en `HibernateJpaAutoConfiguration`). Con los schemas existentes, el
`create-drop` de `@DataJpaTest` puede crear/drop tablas como `trials.tbl_salas_personas`.

---

## Archivos críticos de referencia (NO modificar)

- [SecurityConfig.java](src/main/java/mx/gob/pjpuebla/trials/config/SecurityConfig.java) — tiene `@EnableWebSecurity`, `@RequiredArgsConstructor`, inyecta `DelegatedAuthenticationEntryPoint`
- [DelegatedAuthenticationEntryPoint.java](src/main/java/mx/gob/pjpuebla/trials/config/DelegatedAuthenticationEntryPoint.java) — el componente que falta en `@WebMvcTest`
- [PrimaryDbConfig.java](src/main/java/mx/gob/pjpuebla/config/PrimaryDbConfig.java) — no se carga en `@DataJpaTest`, no necesita cambios
- [application.yaml](src/main/resources/application.yaml) — tiene `PostgreSQLDialect` y `allow_jdbc_metadata_access: false`

---

## Verificación

```bash
# Verificar fix de Resource tests:
mvn test -Dtest="AcuerdoRubrosResourceTest,BloqueResourceTest,ConceptoResourceTest"

# Verificar fix de Repository tests:
mvn test -Dtest="AcuerdoRubrosRepositoryTest,BloqueRepositoryTest,SecuenciaRepositoryCustomTest"

# Suite completa:
mvn clean test
```

**Resultado esperado:** 316 tests cambian de ERROR a PASS. Service tests continúan pasando.
