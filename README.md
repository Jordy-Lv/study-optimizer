# study-optimizer

Backend en Java / Spring Boot para optimizar horas de estudio. Permite registrar
retos propios, sesiones, conceptos, repasos espaciados, analitica, rachas,
logros, metas semanales, errores, sugerencias de estudio, sincronizacion con
GitHub y reporte PDF.

La app trae un seed inicial con los 30 retos de mouredev para tener datos utiles
desde el primer arranque, pero no esta limitada a ellos: el usuario puede crear,
editar y eliminar sus propios retos.

## Stack

- Java 21
- Spring Boot 3.5 (Web, Data JPA, Validation)
- PostgreSQL 16 (Docker Compose)
- Flyway (migraciones)
- Maven
- OpenPDF 1.4.2 para el reporte PDF

## Arquitectura

Organizada **por dominios**. Cada funcionalidad vive en su propio paquete y sigue
el patron **entidad -> repositorio -> service -> controller**, con DTOs como
`record`.

```text
com.jordy.studyoptimizer
├── common/exception   manejo global de errores (404, 409, validacion)
├── exercise           retos editables, done, estimado vs real
├── session            sesiones de estudio
├── concept            catalogo de conceptos + relaciones N:M
├── review             repaso espaciado SM-2
├── analytics          resumen, semanal y por reto
├── streak             rachas de estudio
├── achievement        logros / medallas
├── goal               metas semanales
├── errorlog           banco de errores
├── today              sugeridor "que estudiar hoy"
├── github             lectura/sync de commits
└── report             reporte PDF de progreso
```

La base de datos la gobierna **solo Flyway**. Las entidades JPA se validan contra
el esquema (`spring.jpa.hibernate.ddl-auto=validate`); Hibernate nunca crea ni
altera tablas.

Migraciones:

- `V1__schema.sql` — tablas `exercise` y `study_session`
- `V2__seed_exercises.sql` — seed inicial con 30 retos de mouredev
- `V3__concepts.sql` — `concept`, `exercise_concept`, `session_concept`
- `V4__concept_review.sql` — fichas de repaso espaciado
- `V5__achievements.sql` — logros desbloqueados
- `V6__weekly_goal.sql` — metas semanales
- `V7__error_log.sql` — banco de errores
- `V8__exercise_estimated_minutes.sql` — estimacion de minutos por reto

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

### Retos (`/api/exercises`)

| Metodo | Ruta | Que hace |
|---|---|---|
| GET | `/api/exercises` | lista retos; filtra con `?phase=2` |
| GET | `/api/exercises/{id}` | obtiene un reto |
| POST | `/api/exercises` | crea un reto propio |
| PUT | `/api/exercises/{id}` | actualiza un reto |
| DELETE | `/api/exercises/{id}` | elimina un reto |
| PATCH | `/api/exercises/{id}/done` | marca el reto como hecho |
| POST | `/api/exercises/{id}/concepts` | asocia conceptos (`{"conceptIds":[1,2]}`) |
| DELETE | `/api/exercises/{id}/concepts/{conceptId}` | quita un concepto |
| PATCH | `/api/exercises/{id}/estimate` | guarda el estimado en minutos |
| GET | `/api/exercises/{id}/time` | compara estimado vs real de un reto |
| GET | `/api/exercises/time` | compara estimado vs real de todos los retos estimados |

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
| GET | `/api/concepts/{id}/exercises` | retos que practican ese concepto |
| GET | `/api/concepts/{id}/sessions` | sesiones que repasaron ese concepto |

### Repaso espaciado (`/api/reviews`)

| Metodo | Ruta | Que hace |
|---|---|---|
| GET | `/api/reviews/today` | conceptos a repasar hoy |
| POST | `/api/reviews/concepts/{conceptId}` | registra repaso (`{"quality":0..5}`) |
| GET | `/api/reviews/concepts/{conceptId}` | ficha de repaso del concepto |

### Analitica y rachas

| Metodo | Ruta | Que hace |
|---|---|---|
| GET | `/api/analytics/summary` | resumen global |
| GET | `/api/analytics/weekly` | tiempo agregado por semana |
| GET | `/api/analytics/by-exercise` | tiempo agregado por reto |
| GET | `/api/streak` | racha actual y mas larga |

### Logros, metas y errores

| Metodo | Ruta | Que hace |
|---|---|---|
| GET | `/api/achievements` | estado de todos los logros |
| POST | `/api/achievements/check` | desbloquea logros cumplidos |
| GET | `/api/goals` | lista metas semanales con avance calculado |
| GET | `/api/goals/{id}` | obtiene una meta |
| POST | `/api/goals` | crea una meta |
| PUT | `/api/goals/{id}` | actualiza una meta |
| DELETE | `/api/goals/{id}` | elimina una meta |
| GET | `/api/error-logs` | busca errores con `?q=&exerciseId=&resolved=` |
| GET | `/api/error-logs/{id}` | obtiene un error |
| POST | `/api/error-logs` | crea un error |
| PUT | `/api/error-logs/{id}` | actualiza un error |
| POST | `/api/error-logs/{id}/solution` | registra solucion y marca resuelto |
| DELETE | `/api/error-logs/{id}` | elimina un error |

### Sugerencias, GitHub y reportes

| Metodo | Ruta | Que hace |
|---|---|---|
| GET | `/api/today?minutes=60&energy=MEDIUM` | sugiere que estudiar hoy |
| GET | `/api/github/commits` | lee commits y detecta retos mencionados |
| POST | `/api/github/sync?dryRun=true` | sincroniza commits con retos hechos |
| GET | `/api/reports/progress` | descarga reporte PDF de progreso |

## Configuracion GitHub

La integracion usa valores por defecto en `application.yml` y permite override por
query params:

- `GITHUB_OWNER`
- `GITHUB_REPO`
- `GITHUB_TOKEN` opcional, recomendado para evitar limites de rate.

`POST /api/github/sync?dryRun=true` permite previsualizar sin escribir en la BD.

## Roadmap

| # | Milestone | Estado |
|---|---|---|
| — | Base (retos + sesiones) | ✅ |
| 1 | Conceptos (catalogo + N:M) | ✅ |
| 2 | Repaso espaciado (SM-2) | ✅ |
| 3 | Pomodoro + analitica semanal | ✅ |
| 4 | Rachas | ✅ |
| 5 | Logros / medallas | ✅ |
| 6 | Metas semanales | ✅ |
| 7 | Banco de errores | ✅ |
| 8 | Estimado vs real | ✅ |
| 9 | "Que estudiar hoy" | ✅ |
| 10 | Integracion GitHub | ✅ |
| 11 | Reporte PDF | ✅ |

## Tests

```bash
mvn test
```

La suite actual cubre el servicio de retos editables, el matcher de commits de
GitHub y el sugeridor de estudio.
