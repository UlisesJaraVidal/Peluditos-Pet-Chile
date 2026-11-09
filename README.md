# Peluditos-Pet-Chile
## Entrega 1 
Gestion de leads y oportunidades comerciales(BPMN+ Flowable + Java)
El servidor rechaza la solicitud con un error HTTP 401/403 debido a restricciones de la cuenta institucional.
https://youtu.be/vestnSPsK40 Vinculo video para PYME entrega 1.
https://youtu.be/L8F82taI7QQ Vinculo video a profesor entrega 1. 

## Entrega 2 - Arquitectura y Tecnologías

Para esta entrega se migró de Flowable Enterprise a un entorno **Flowable OSS** local utilizando **Docker**.

### Componentes Principales
- **Flowable OSS (Docker - Puerto 8080):** Motor de procesos ejecutando el BPMN TO-BE.
- **PostgreSQL (Docker - Puerto 5432):** Base de datos relacional que aloja tanto el esquema de Flowable como la base de datos de negocio (`peluditos_db`).
- **External Worker (Spring Boot - Puerto 8081):** Escucha las tareas del topic `registrarLeadTopic` y realiza peticiones HTTP.
- **Web Service JAX-RS (Spring Boot - Puerto 8082):** Implementado con Jersey (Jakarta EE). Recibe los datos del Worker y realiza un `INSERT` en PostgreSQL usando `JdbcTemplate`.
- **Interfaz Web Propia (Planificada):** Se implementará una bandeja de tareas conectada a la API REST de Flowable OSS.

### Persistencia de Datos
La tabla `clientes` en PostgreSQL almacena los datos de negocio (Nombre del cliente, etc.) demostrando que los datos sobreviven al reinicio de los componentes.

### Instrucciones de Ejecución (Local)
1. Levantar la infraestructura: `docker compose up -d`
2. Ejecutar el Worker: `cd External-Worker && java -jar target/worker-0.0.1-SNAPSHOT.jar`
3. Ejecutar el Web Service JAX-RS: `cd Backend-Webservices && .\mvnw.cmd spring-boot:run "-Dspring-boot.run.arguments=--server.port=8082"`