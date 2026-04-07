package com.jordy.studyoptimizer.analytics.dto;

public record ExerciseStatsResponse(
        long exerciseId,
        int dayNumber,
        String title,
        long totalMinutes,
        double totalHours,
        long sessions,
        long pomodoros
) {
}
