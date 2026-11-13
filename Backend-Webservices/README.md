
# Backend Web Services - Peluditos Pet Chile

## Evaluación 2: Integración con PostgreSQL

Se implementó la conexión del servicio de fechas especiales con una base de datos PostgreSQL, utilizando Java, Spring Boot, Jakarta REST (JAX-RS), Jersey y JDBC.

## Tecnologías utilizadas

- Java 21 (compilación)
- Spring Boot 4.1.1
- Jakarta REST (JAX-RS)
- Jersey
- Spring JDBC
- PostgreSQL 17
- Maven

## Base de datos

Nombre: peluditos_db

Puerto: 5432

El archivo `database/init_peluditos.sql` contiene las instrucciones para crear las tablas:

- clientes
- mascotas
- fechas_especiales

También incorpora datos ficticios para las pruebas.

## Configuración

1. Instalar PostgreSQL.
2. Crear la base de datos `peluditos_db`.
3. Ejecutar `database/init_peluditos.sql` en pgAdmin.
4. Configurar la variable de entorno PELUDITOS_DB_PASSWORD con la contraseña local de PostgreSQL.

No se deben publicar contraseñas en GitHub.

## Ejecución en Windows

Desde la carpeta Backend-Webservices:

Ejecutar las pruebas:

    .\mvnw.cmd test

Iniciar la aplicación:

    .\mvnw.cmd spring-boot:run

La variable de entorno debe estar configurada antes de iniciar la aplicación.

## Servicio implementado

GET /api/clientes/fechas-proximas

URL local:

http://localhost:8080/api/clientes/fechas-proximas

El servicio consulta PostgreSQL mediante JdbcTemplate y devuelve información de clientes, mascotas y fechas especiales en formato JSON.

Actualmente devuelve las fechas registradas, ordenadas cronológicamente.

## Pruebas realizadas

- Compilación Maven: BUILD SUCCESS.
- Pruebas automáticas: 2 exitosas, 0 fallos.
- Inicio correcto de Spring Boot.
- Consulta HTTP del servicio JAX-RS.
- Lectura de datos almacenados en PostgreSQL.

## Evidencias

Las capturas y el video de comprobaciones se encuentran en:

Videos/Entrega-2

## Pendiente de integración grupal

- Integración del servicio con Flowable OSS.
- Pruebas del recorrido completo del sistema.
- Ajustes finales de la solución.

- 
## Configuración segura de PostgreSQL

La aplicación utiliza variables de entorno para
conectarse a PostgreSQL sin publicar credenciales.

Variables requeridas:
- PELUDITOS_DB_USER
- PELUDITOS_DB_PASSWORD

### Ejecución local en Windows

Abrir PowerShell dentro de Backend-Webservices.

Configurar el usuario local:

    $env:PELUDITOS_DB_USER = "postgres"

Ingresar la contraseña de forma segura:

    $secure = Read-Host "Contraseña PostgreSQL" -AsSecureString
    $env:PELUDITOS_DB_PASSWORD = [System.Net.NetworkCredential]::new("", $secure).Password

Iniciar la aplicación:

    .\mvnw.cmd spring-boot:run

La configuración del entorno Docker debe
coordinarse con el responsable de Flowable.

No publicar contraseñas en GitHub.

### Servicios disponibles

GET /api/clientes/fechas-proximas
Consulta las fechas especiales almacenadas.

POST /api/clientes/registrar
Recibe información de clientes para su registro.

### Integración con Flowable OSS

El External Worker invoca el servicio JAX-RS.
Las pruebas integradas finales deben verificarse
con el entorno compartido del equipo.

