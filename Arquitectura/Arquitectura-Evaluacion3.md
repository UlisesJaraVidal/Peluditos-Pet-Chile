# Arquitectura - Evaluacion 3

## Peluditos Pet Chile

```mermaid
flowchart LR
    U["Usuario"] --> F["Frontend HTML, CSS y JavaScript"]
    F -->|REST| R["Flowable REST - Puerto 8080"]
    R <-->|Tareas BPMN| W["External Worker - Puerto 8081"]
    W -->|POST registrar Lead| B["Backend JAX-RS - Puerto 8082"]
    R <-->|Persistencia BPMN| DB1[("PostgreSQL Flowable - Puerto 5432")]
    B <-->|Persistencia de clientes| DB2[("PostgreSQL Peluditos - Puerto 5433")]
```

## Componentes

- Frontend: registro de Leads y gestion de tareas.
- Flowable REST: ejecucion de procesos BPMN.
- External Worker: comunicacion entre Flowable y Backend.
- Backend JAX-RS: registro y consulta de clientes.
- PostgreSQL Flowable: persistencia de procesos.
- PostgreSQL Peluditos: persistencia de datos del negocio.

## Limitaciones conocidas

El proceso de Leads fue probado de extremo a extremo.

El proceso de Oportunidades contiene tareas automaticas
simuladas. La integracion real de notificaciones queda
pendiente para la Entrega 4.

Las credenciales se configuran mediante variables
de entorno y no deben publicarse.
