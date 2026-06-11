package com.jordy.studyoptimizer.analytics.dto;

/** Resumen global del estudio. */
public record SummaryResponse(
        long totalMinutes,
        double totalHours,
        long totalSessions,
        long distinctDays,
        long pomodoros,
        double avgDifficulty,
        long totalExercises,
        long completedExercises
) {
}
