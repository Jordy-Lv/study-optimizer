-- V9: poda de modulos de relleno. La app se queda con su nucleo de estudio
-- (retos, sesiones, conceptos, repaso espaciado, banco de errores, analitica).
-- Se eliminan los esquemas de logros (V5) y metas semanales (V6), y la columna
-- de tiempo estimado (V8). Las migraciones V5/V6/V8 NO se tocan (regla Flyway:
-- nunca editar una migracion ya aplicada); esta nueva migracion revierte su efecto.

DROP TABLE IF EXISTS unlocked_achievement;
DROP TABLE IF EXISTS weekly_goal;

ALTER TABLE exercise DROP COLUMN IF EXISTS estimated_minutes;
