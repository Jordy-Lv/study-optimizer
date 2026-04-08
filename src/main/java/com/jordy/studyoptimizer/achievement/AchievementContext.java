package com.jordy.studyoptimizer.achievement;

/**
 * Foto del progreso actual del usuario. Es la entrada que cada regla de logro
 * evalua. Se arma combinando metricas de varios dominios (exercise, streak,
 * analytics) en un solo objeto inmutable.
 */
public record AchievementContext(
        long completedExercises,
        long completedPhases,
        int currentStreak,
        int longestStreak,
        long totalPomodoros,
        long totalMinutes,
        long totalSessions,
        long distinctStudyDays
) {
}
