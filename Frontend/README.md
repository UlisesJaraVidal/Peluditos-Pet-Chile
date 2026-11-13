
# Frontend - Peluditos Pet Chile

## Evaluación 2

Interfaz web para registrar clientes y gestionar
tareas mediante Flowable REST.

## Configuración local

1. Copiar el archivo config.example.js.
2. Renombrar la copia como config.js.
3. Abrir config.js y configurar:
   - flowableUser: usuario de Flowable.
   - flowablePass: contraseña de Flowable.
4. Verificar que Flowable REST esté funcionando
   en http://localhost:8080.
5. Ejecutar el Frontend con un servidor local,
   utilizando el puerto 8000 o 5173.

## Archivos principales

- Index.html: estructura de la interfaz.
- styles.css: diseño visual.
- app.js: comunicación con Flowable REST.
- config.example.js: ejemplo de configuración.
- config.js: configuración local privada.

## Seguridad

No subir config.js ni contraseñas reales a GitHub.

IMPORTANTE: Las credenciales configuradas en
JavaScript son visibles desde el navegador.
Esta configuración es únicamente para pruebas
locales. Para un entorno real debe utilizarse
autenticación segura desde un Backend.

## Pruebas pendientes

Verificar el funcionamiento completo del
Frontend, Flowable, External Worker,
Web Service JAX-RS y PostgreSQL.
