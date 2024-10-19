# trials-service

## Requisitos

- JDK21
- PostgreSQL 16
- Keycloak 25

### Running as Developer using a dockerized PostgreSQL Database and Keycloak

```shell
mvn clean package -Dmaven.test.skip
mvn spring-boot:run -Dspring-boot.run.arguments=--spring.flyway.enabled=true

```

### Nomenclatura de Bases de datos
tbl = table -> TBL_NAME
seq = sequence -> SEQ_NAME
i = index -> iNOMBRE
pk = primary key -> p|TIPO|NOMBRE -> psid
fk = foreign key -> f|TIPO|NOMBRE -> fsdomicilio

#### Tipos De Datos -> Columnas
s = string -> uuid, varchar, char
t = temporal -> timestamp, date, time
n = numeric -> int, bigint
b = boolean -> no usar, reemplazar por char(1) A=Active, I=Inactive, D=Deleted
