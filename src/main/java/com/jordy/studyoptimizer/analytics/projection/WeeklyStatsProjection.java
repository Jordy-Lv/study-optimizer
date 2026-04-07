package com.jordy.studyoptimizer.analytics.projection;

import java.time.LocalDate;

/** Estadisticas agregadas de una semana (la semana empieza el lunes). */
public interface WeeklyStatsProjection {
    LocalDate getWeekStart();
    Long getTotalMinutes();
    Long getSessions();
    Double getAvgDifficulty();
}
