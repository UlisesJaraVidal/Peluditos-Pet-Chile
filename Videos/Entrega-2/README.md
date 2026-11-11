# Evidencias – Evaluación 2

**Proyecto:** Peluditos Pet Chile  
**Responsable:** Bastián Fernández Garrido

## Servicios implementados

Se adaptó el servicio de fechas especiales a Jakarta REST (JAX-RS), utilizando Jersey y Spring Boot.

Se implementaron dos operaciones:

- **GET /api/clientes/fechas-proximas:** consulta información de clientes, mascotas y fechas especiales desde PostgreSQL.
- **POST /api/clientes/registrar:** recibe información de clientes y permite registrarlos mediante JdbcTemplate.

## Pruebas realizadas

- Compilación Maven: BUILD SUCCESS.
- Pruebas automáticas: 3 exitosas, 0 fallos y 0 errores.
- Inicio correcto de Spring Boot.
- Consulta GET con respuesta JSON desde PostgreSQL.
- Prueba automatizada del POST utilizando JDBC simulado.

Adicionalmente, el equipo informó que comprobó el registro de un cliente desde el servicio Java, verificando su almacenamiento en PostgreSQL.

## Evidencias audiovisuales

Los videos técnicos y las capturas de comprobación se encuentran en esta carpeta, incluyendo las pruebas JAX-RS, Maven, PostgreSQL y configuración de Docker.

## Seguridad e integración

Se incorporaron variables de entorno, un archivo `.gitignore` y un archivo `.env.example` para mejorar el manejo de las credenciales.

Queda pendiente renovar las credenciales expuestas anteriormente y comprobar nuevamente el recorrido completo del sistema con la configuración segura.

La integración final con Flowable OSS, External Worker y Frontend requiere confirmación antes del merge a la rama principal.
