-- V8: tiempo ESTIMADO por reto, para compararlo con el tiempo REAL.
-- El real NO se guarda aqui: se calcula al leer sumando los minutos de las
-- sesiones del reto (study_session.minutes). Asi no duplicamos estado que
-- se pueda desincronizar (ver gotcha "metricas derivadas" en CLAUDE.md).
-- Nullable a proposito: no todos los retos tienen estimacion todavia.
ALTER TABLE exercise ADD COLUMN estimated_minutes INTEGER;
