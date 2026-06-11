# study-optimizer

Backend en Java / Spring Boot para optimizar horas de estudio. Permite registrar
ejercicios, sesiones, conceptos, repasos espaciados, analitica y un banco de
errores.

La app trae un seed inicial con 30 ejercicios de ejemplo para tener datos utiles
desde el primer arranque, pero no esta limitada a ellos: el usuario puede crear,
editar y eliminar sus propios ejercicios.

## Stack

- Java 21
- Spring Boot 3.5 (Web, Data JPA, Validation)
- PostgreSQL 16 (Docker Compose)
- Flyway (migraciones)
- Maven

## Arquitectura

Organizada **por dominios**. Cada funcionalidad vive en su propio paquete y sigue
el patron **entidad -> repositorio -> service -> controller**, con DTOs como
`record`.

```text
com.jordy.studyoptimizer
├── common/exception   manejo global de errores (404, 409, validacion)
├── exercise           ejercicios editables, marcado como hechos
├── session            sesiones de estudio
├── concept            catalogo de conceptos + relaciones N:M
├── review             repaso espaciado SM-2
├── analytics          resumen, semanal y por ejercicio
└── errorlog           banco de errores
```

La base de datos la gobierna **solo Flyway**. Las entidades JPA se validan contra
el esquema (`spring.jpa.hibernate.ddl-auto=validate`); Hibernate nunca crea ni
altera tablas.

Migraciones:

- `V1__schema.sql` — tablas `exercise` y `study_session`
- `V2__seed_exercises.sql` — seed inicial con 30 ejercicios de ejemplo
- `V3__concepts.sql` — `concept`, `exercise_concept`, `session_concept`
- `V4__concept_review.sql` — fichas de repaso espaciado
- `V7__error_log.sql` — banco de errores
- `V9__drop_unused_modules.sql` — poda de modulos no esenciales (logros, metas, estimado)

## Arrancar en local

```bash
# 1. Levantar PostgreSQL
docker compose up -d

# 2. Arrancar la aplicacion (Flyway corre las migraciones al inicio)
mvn spring-boot:run
```

La API queda en `http://localhost:8080`. Credenciales de la BD para desarrollo en
`docker-compose.yml`; la app las lee por variables de entorno con valores por
defecto.

Reiniciar tras cambios:

```bash
lsof -ti tcp:8080 | xargs kill 2>/dev/null; sleep 2; mvn spring-boot:run > /tmp/study-app.log 2>&1 &
```

## Endpoints principales

### Ejercicios (`/api/exercises`)

| Metodo | Ruta | Que hace |
|---|---|---|
| GET | `/api/exercises` | lista ejercicios; filtra con `?phase=2` |
| GET | `/api/exercises/{id}` | obtiene un ejercicio |
| POST | `/api/exercises` | crea un ejercicio propio |
| PUT | `/api/exercises/{id}` | actualiza un ejercicio |
| DELETE | `/api/exercises/{id}` | elimina un ejercicio |
| PATCH | `/api/exercises/{id}/done` | marca el ejercicio como hecho |
| POST | `/api/exercises/{id}/concepts` | asocia conceptos (`{"conceptIds":[1,2]}`) |
| DELETE | `/api/exercises/{id}/concepts/{conceptId}` | quita un concepto |

### Sesiones (`/api/sessions`)

| Metodo | Ruta | Que hace |
|---|---|---|
| POST | `/api/sessions` | registra una sesion |
| GET | `/api/sessions` | lista sesiones; filtra con `?exerciseId=3` |
| GET | `/api/sessions/{id}` | obtiene una sesion |
| POST | `/api/sessions/{id}/concepts` | asocia conceptos |
| DELETE | `/api/sessions/{id}/concepts/{conceptId}` | quita un concepto |
| DELETE | `/api/sessions/{id}` | elimina una sesion |

### Conceptos (`/api/concepts`)

| Metodo | Ruta | Que hace |
|---|---|---|
| POST | `/api/concepts` | crea un concepto |
| GET | `/api/concepts` | lista conceptos |
| GET | `/api/concepts/{id}` | obtiene un concepto |
| PUT | `/api/concepts/{id}` | actualiza un concepto |
| DELETE | `/api/concepts/{id}` | elimina un concepto |
| GET | `/api/concepts/{id}/exercises` | ejercicios que practican ese concepto |
| GET | `/api/concepts/{id}/sessions` | sesiones que repasaron ese concepto |

### Repaso espaciado (`/api/reviews`)

| Metodo | Ruta | Que hace |
|---|---|---|
| GET | `/api/reviews/today` | conceptos a repasar hoy |
| POST | `/api/reviews/concepts/{conceptId}` | registra repaso (`{"quality":0..5}`) |
| GET | `/api/reviews/concepts/{conceptId}` | ficha de repaso del concepto |

### Analitica (`/api/analytics`)

| Metodo | Ruta | Que hace |
|---|---|---|
| GET | `/api/analytics/summary` | resumen global |
| GET | `/api/analytics/weekly` | tiempo agregado por semana |
| GET | `/api/analytics/by-exercise` | tiempo agregado por ejercicio |

### Banco de errores (`/api/error-logs`)

| Metodo | Ruta | Que hace |
|---|---|---|
| GET | `/api/error-logs` | busca errores con `?q=&exerciseId=&resolved=` |
| GET | `/api/error-logs/{id}` | obtiene un error |
| POST | `/api/error-logs` | crea un error |
| PUT | `/api/error-logs/{id}` | actualiza un error |
| POST | `/api/error-logs/{id}/solution` | registra solucion y marca resuelto |
| DELETE | `/api/error-logs/{id}` | elimina un error |

## Funcionalidades

| Area | Estado |
|---|---|
| Ejercicios + sesiones | ✅ |
| Conceptos (catalogo + N:M) | ✅ |
| Repaso espaciado (SM-2) | ✅ |
| Pomodoro + analitica semanal | ✅ |
| Banco de errores | ✅ |

## Tests

```bash
mvn test
```

La suite actual cubre el servicio de ejercicios editables.
