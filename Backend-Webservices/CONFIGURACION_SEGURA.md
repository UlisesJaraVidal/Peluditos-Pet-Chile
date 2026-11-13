
# Configuración segura de PostgreSQL

## Peluditos Pet Chile - Evaluación 2

Esta guía permite ejecutar Spring Boot con
PostgreSQL sin publicar contraseñas en GitHub.

### 1. Abrir PowerShell

Abrir PowerShell dentro de Backend-Webservices.

### 2. Configurar el usuario

Para PostgreSQL local:

    $env:PELUDITOS_DB_USER = "postgres"

Para el usuario configurado por el equipo
en Docker, utilizar "flowable" si corresponde.

### 3. Configurar la contraseña

Ejecutar:

    $secure = Read-Host "Contraseña PostgreSQL" -AsSecureString

    $env:PELUDITOS_DB_PASSWORD = [System.Net.NetworkCredential]::new("", $secure).Password

La contraseña se introduce sin mostrarla
en pantalla y no se guarda en GitHub.

### 4. Ejecutar Spring Boot

Si el puerto 8080 está disponible:

    .\mvnw.cmd spring-boot:run

Si Flowable utiliza el puerto 8080,
se puede configurar previamente:

    $env:SERVER_PORT = "8082"

Y ejecutar el mismo comando Maven.

### 5. Comprobar los servicios

GET /api/clientes/fechas-proximas

POST /api/clientes/registrar

Utilizar el puerto correspondiente.

### 6. Resultado comprobado

En las pruebas locales se verificó:

- Inicio correcto de Spring Boot.
- Consulta GET desde PostgreSQL.
- Registro POST de un cliente.
- Confirmación del registro en pgAdmin.

### 7. Consideraciones importantes

Las variables deben configurarse en la
misma sesión de PowerShell que inicia Java.

El entorno Docker puede utilizar credenciales
y usuarios diferentes.

Las contraseñas anteriormente publicadas
deben renovarse antes de la integración final.

No subir archivos que contengan
contraseñas reales al repositorio.
