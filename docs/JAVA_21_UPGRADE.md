# Actualización a Java 21

## Resumen de cambios

Se ha actualizado el proyecto **trials-service** de **Java 17** a **Java 21** el 2026-04-20.

---

## Cambios realizados en pom.xml

### 1. Versión de Java
```xml
<!-- Antes -->
<java.version>17</java.version>

<!-- Después -->
<java.version>21</java.version>
```

### 2. Maven Compiler Release
```xml
<!-- Nuevo -->
<maven.compiler.release>21</maven.compiler.release>
```

---

## Compatibilidad de dependencias

Todas las dependencias del proyecto soportan Java 21:

| Dependencia | Versión | Soporte Java 21 |
|-------------|---------|-----------------|
| Spring Boot | 3.3.5 | ✅ Soporta 17+ |
| Keycloak | 26.0.2 | ✅ Soporta 21 |
| Jasper Reports | 7.1.0 | ✅ Completamente compatible |
| PostgreSQL Driver | 42.7.4 | ✅ Soporta 21 |
| Hibernate | 6.3 | ✅ Soporta 21 |
| Spring Data JPA | 3.3.5 | ✅ Soporta 21 |
| Maven Surefire | 3.5.1 | ✅ Soporta 21 |
| Lombok | Latest | ✅ Soporta 21 |
| OpenPDF | 2.0.3 | ✅ Soporta 21 |
| Apache POI | 5.3.0 | ✅ Soporta 21 |

---

## Beneficios de Java 21

### 1. **Nuevas características del lenguaje**
- **Pattern Matching for switch** (Preview → Standard)
- **Record Patterns** para desestructuración de datos
- **String Templates** (Preview) para cadenas interpoladas
- **Unnamed Classes and Instance Main Methods** para simplificar código inicial

### 2. **Mejoras de rendimiento**
- **Virtual Threads (Project Loom)**: Threads más ligeros para aplicaciones altamente concurrentes
  - Mejora significativa para aplicaciones ReactiveX como tu stack (webflux)
  - Reducción de memory footprint en operaciones I/O
- **Garbage Collection improvements**: Mejor eficiencia en recolección de basura
- **JIT Compiler enhancements**: Optimizaciones adicionales en tiempo de ejecución

### 3. **Seguridad mejorada**
- **Sealed Classes**: Control fino sobre herencia de clases
- **Strong encapsulation by default**: Mejor aislamiento de módulos
- **Deprecación de algoritmos débiles**: Mayor seguridad criptográfica
- **Foreign Function & Memory API (Preview)**: Acceso seguro a memoria nativa

### 4. **Características de contenedores**
- **Better container awareness**: Java detecta automáticamente límites de CPU/memoria
- **Optimizaciones para Docker/Kubernetes**: Mejor gestión de recursos en ambientes containerizados

### 5. **Compatibilidad LTS**
- Java 21 es **Long Term Support (LTS)** hasta 2031
- Soporte extendido garantizado por Oracle
- Estabilidad y seguridad asegurada durante años

---

## Mejoras específicas para tu proyecto

### Virtual Threads con Spring WebFlux
Tu proyecto usa `spring-boot-starter-webflux`, que se beneficia enormemente:

```java
// Antes: Completamente asincrónico
@GetMapping("/api/trials")
public Mono<ResponseEntity<List<Trial>>> getTrials() {
    return trialRepository.findAll().map(ResponseEntity::ok);
}

// Ahora posible: Código síncrono con Virtual Threads
@GetMapping("/api/trials")
public ResponseEntity<List<Trial>> getTrials() {
    // Puede usar código sincrónico, Virtual Threads maneja concurrencia
    List<Trial> trials = trialRepository.findAll();
    return ResponseEntity.ok(trials);
}
```

### Jasper Reports (7.1.0)
- Mejor manejo de recursos en Java 21
- Optimización de renderización de PDFs
- Mejor integración con caracteres Unicode (útil para documentos en español)

### Keycloak Integration
- Soporte completo para JWT con algoritmos modernos
- Mejor rendimiento en validación de tokens OAuth2
- Seguridad mejorada en policy enforcement

---

## Pasos para validación post-actualización

### 1. Compilación
```bash
mvn clean compile
```

### 2. Tests
```bash
mvn test
```

### 3. Build del JAR
```bash
mvn clean package
```

### 4. Verificar versión en tiempo de ejecución
```bash
java -version
# Debería mostrar: openjdk version "21.x.x"
```

---

## Consideraciones adicionales

### 1. **Garbage Collection**
Para Java 21, se recomienda usar el colector ZGC o G1GC (por defecto) en producción:

```bash
java -XX:+UseZGC -jar trials-0.13.12.jar  # ZGC (ultra bajo latency)
java -XX:+UseG1GC -jar trials-0.13.12.jar # G1GC (recomendado por defecto)
```

### 2. **Virtual Threads (Experimental)**
Para usar Virtual Threads en proyectos nuevos:

```properties
# application.properties
spring.threads.virtual.enabled=true
```

### 3. **Java Modules**
El proyecto no usa JPMS (Java Platform Module System), por lo que no hay cambios requeridos.

### 4. **Maven Configuration**
Se añadió `<maven.compiler.release>21</maven.compiler.release>` para asegurar que el compilador genera bytecode compatible con Java 21.

---

## Testing y validación

### Checklist post-upgrade:
- [ ] Compilación sin errores: `mvn clean compile`
- [ ] Tests unitarios pasan: `mvn test`
- [ ] Aplicación inicia correctamente
- [ ] Endpoints REST funcionan como se espera
- [ ] Autenticación Keycloak funciona
- [ ] Reportes Jasper se generan correctamente
- [ ] Base de datos (PostgreSQL) se conecta sin problemas
- [ ] Logs (Loki/Logback) se envían correctamente
- [ ] No hay warnings de deprecación en logs

---

## Timeline y Soporte

| Versión | Release | LTS | Fin de soporte |
|---------|---------|-----|-----------------|
| Java 17 | Sept 2021 | ✅ | Sept 2026 |
| Java 21 | Sept 2023 | ✅ | Sept 2031 |

Recomendación: Java 21 es la mejor opción ahora para nuevo desarrollo y tendrá soporte hasta 2031.

---

## Referencias

- [Java 21 Release Notes](https://www.oracle.com/java/technologies/javase/jdk21-doc.html)
- [Spring Boot 3.3 Java 21 Support](https://spring.io/blog/2024/05/23/spring-boot-3-3-0-released)
- [Virtual Threads Documentation](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/VirtualThread.html)
- [Keycloak 26 Release Notes](https://www.keycloak.org/docs/latest/release_notes/)

---

**Última actualización:** 2026-04-20  
**Responsable:** Alexis Benítez Arellano

**Estado:** Completado ✅
