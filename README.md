
# Peluditos Pet Chile

Proyecto de gestión de leads y oportunidades
comerciales mediante BPMN, Flowable y Java.

## Entrega 1

Se realizó el modelado de procesos AS-IS y
TO-BE y las primeras pruebas de integración.

Se documentaron las restricciones HTTP 401/403
encontradas con la cuenta institucional.

Videos de Entrega 1:
- PYME: https://youtu.be/vestnSPsK40
- Profesor: https://youtu.be/L8F82taI7QQ

## Entrega 2 - Arquitectura y tecnologías

Se implementó una solución local utilizando
Flowable OSS, Docker, PostgreSQL y Java.

### Componentes implementados

- Flowable REST 7.2.0: motor de procesos BPMN.
- PostgreSQL: persistencia de información.
- External Worker: recibe tareas de Flowable.
- Web Service JAX-RS: registra clientes mediante
  Spring Boot, Jersey y JdbcTemplate.
- Frontend HTML, CSS y JavaScript: interfaz
  propia para registrar leads y gestionar tareas.

### Funcionamiento

1. El usuario registra un lead en el Frontend.
2. Flowable inicia y gestiona el proceso BPMN.
3. El External Worker recibe la tarea.
4. El Worker llama al servicio JAX-RS.
5. El Backend registra el cliente en PostgreSQL.

El equipo informó que validó este recorrido
completo en su entorno local.

### Servicios principales

GET /api/clientes/fechas-proximas

POST /api/clientes/registrar

### Configuración local

La infraestructura utiliza Docker Compose.

La base de datos de negocio peluditos_db y
sus tablas deben estar creadas en PostgreSQL.

Las contraseñas deben configurarse localmente,
sin publicarlas en el repositorio.

Consultar:
- Backend-Webservices/README.md
- Backend-Webservices/CONFIGURACION_SEGURA.md
- Frontend/README.md
- .env.example

### Pruebas realizadas

- Compilación Maven: BUILD SUCCESS.
- Última ejecución: 2 pruebas exitosas.
- GET consultado desde PostgreSQL.
- POST confirmado mediante pgAdmin.
- Integración completa validada por Ulises.

### Evidencias

Consultar la carpeta Videos/Entrega-2.

Contiene videos técnicos, capturas de Maven,
pruebas PostgreSQL y documentación.

### Cierre de la evaluación

Pendiente:
- Incorporar el video de la emprendedora.
- Completar la revisión de credenciales.
- Revisar y aprobar la PR #2.
