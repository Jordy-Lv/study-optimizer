# study-optimizer

Backend en Java / Spring Boot para optimizar horas de estudio: lleva el registro
de los 30 retos de mouredev, las sesiones de estudio y los conceptos que practicas,
con la idea de construir encima repaso espaciado, analitica, rachas y mas.

## Stack

- Java 21
- Spring Boot 3.5 (Web, Data JPA, Validation)
- PostgreSQL 16 (vía Docker Compose)
- Flyway (migraciones)
- Maven

## Arquitectura

Organizada **por dominios**. Cada funcionalidad vive en su propio paquete y sigue el
patron **entidad → repositorio → service → controller**, con DTOs como `record`.

```
com.jordy.studyoptimizer
├── common/exception   manejo global de errores (404, 409, validacion)
├── exercise           los 30 retos de mouredev
├── session            sesiones de estudio
└── concept            catalogo de conceptos + relaciones N:M  (milestone 1)
```

La base de datos la gobierna **solo Flyway**. Las entidades JPA se validan contra el
esquema (`spring.jpa.hibernate.ddl-auto=validate`); Hibernate nunca crea ni altera tablas.

Migraciones:
- `V1__schema.sql`  — tablas `exercise` y `study_session`
- `V2__seed_exercises.sql` — carga los 30 retos
- `V3__concepts.sql` — tabla `concept` y puentes `exercise_concept`, `session_concept`

## Arrancar en local

```bash
# 1. Levantar PostgreSQL
docker compose up -d

# 2. Arrancar la aplicacion (Flyway corre las migraciones al inicio)
mvn spring-boot:run
```

La API queda en `http://localhost:8080`. Credenciales de la BD para desarrollo en
`docker-compose.yml`; la app las lee por variables de entorno con valores por defecto.

## Endpoints principales

### Retos (`/api/exercises`)
| Metodo | Ruta | Que hace |
|---|---|---|
| GET | `/api/exercises` | lista todos (filtra con `?phase=2`) |
| GET | `/api/exercises/{id}` | un reto |
| PATCH | `/api/exercises/{id}/done` | marca el reto como hecho |
| POST | `/api/exercises/{id}/concepts` | asocia conceptos (`{"conceptIds":[1,2]}`) |
| DELETE | `/api/exercises/{id}/concepts/{conceptId}` | quita un concepto |

### Sesiones (`/api/sessions`)
| Metodo | Ruta | Que hace |
|---|---|---|
| POST | `/api/sessions` | registra una sesion |
| GET | `/api/sessions` | lista (filtra con `?exerciseId=3`) |
| GET | `/api/sessions/{id}` | una sesion |
| POST | `/api/sessions/{id}/concepts` | asocia conceptos |
| DELETE | `/api/sessions/{id}/concepts/{conceptId}` | quita un concepto |
| DELETE | `/api/sessions/{id}` | borra |

### Conceptos (`/api/concepts`)  — milestone 1
| Metodo | Ruta | Que hace |
|---|---|---|
| POST | `/api/concepts` | crea un concepto |
| GET | `/api/concepts` | lista |
| GET | `/api/concepts/{id}` | uno |
| PUT | `/api/concepts/{id}` | actualiza |
| DELETE | `/api/concepts/{id}` | borra |
| GET | `/api/concepts/{id}/exercises` | retos que lo practican |
| GET | `/api/concepts/{id}/sessions` | sesiones que lo repasaron |

### Repaso espaciado (`/api/reviews`)  — milestone 2 (SM-2)
| Metodo | Ruta | Que hace |
|---|---|---|
| GET | `/api/reviews/today` | conceptos a repasar hoy (nuevos + vencidos) |
| POST | `/api/reviews/concepts/{conceptId}` | registra un repaso (`{"quality":0..5}`) |
| GET | `/api/reviews/concepts/{conceptId}` | ficha de repaso del concepto |

`quality`: 0 = no recordaste nada ... 5 = perfecto. Con < 3 la ficha se reinicia
(repasar manana); con >= 3 el intervalo crece (1 dia, 6 dias, luego x factor de facilidad).

### Analitica (`/api/analytics`)  — milestone 3 (Pomodoro + estadisticas)
| Metodo | Ruta | Que hace |
|---|---|---|
| GET | `/api/analytics/summary` | resumen global (minutos, horas, sesiones, dias, pomodoros) |
| GET | `/api/analytics/weekly` | tiempo de estudio agregado por semana (lunes) |
| GET | `/api/analytics/by-exercise` | tiempo de estudio agregado por reto |

Un "pomodoro" se cuenta como 25 minutos. Las agregaciones usan `SUM`/`AVG`/`COUNT`
con `GROUP BY` (queries nativas de PostgreSQL para agrupar por semana ISO).

### Rachas (`/api/streak`)  — milestone 4
| Metodo | Ruta | Que hace |
|---|---|---|
| GET | `/api/streak` | racha actual y mas larga de dias seguidos estudiando |

Se calcula a partir de los dias distintos con sesiones. La racha actual cuenta hacia
atras desde hoy (con 1 dia de gracia: sigue viva si estudiaste ayer).

### Logros (`/api/achievements`)  — milestone 5
| Metodo | Ruta | Que hace |
|---|---|---|
| GET | `/api/achievements` | estado de todos los logros (no modifica nada) |
| POST | `/api/achievements/check` | evalua las reglas y desbloquea los recien cumplidos |

Las reglas viven en codigo (`AchievementCode`, un enum con un predicado por logro).
Solo se persisten los logros desbloqueados. `check` devuelve `newlyUnlocked` + `all`.

## Roadmap

El estado detallado vive en [CLAUDE.md](CLAUDE.md) (fuente de verdad del progreso).

1. ✅ Conceptos (catalogo + N:M)
2. ✅ Repaso espaciado (SM-2)
3. ✅ Pomodoro + analitica semanal
4. ✅ Rachas
5. ✅ Logros / medallas
6. ⬜ Metas semanales  ← siguiente
7. ⬜ Banco de errores
8. ⬜ Estimado vs real
9. ⬜ "Que estudiar hoy"
10. ⬜ Integracion GitHub
11. ⬜ Reporte PDF
