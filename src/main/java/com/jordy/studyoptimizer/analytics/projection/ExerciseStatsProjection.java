package com.jordy.studyoptimizer.analytics.projection;

/** Estadisticas agregadas de tiempo de estudio por ejercicio. */
public interface ExerciseStatsProjection {
    Long getExerciseId();
    Integer getDayNumber();
    String getTitle();
    Long getTotalMinutes();
    Long getSessions();
}
