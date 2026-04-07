package com.jordy.studyoptimizer.analytics.dto;

import java.time.LocalDate;

public record WeeklyStatsResponse(
        LocalDate weekStart,
        long totalMinutes,
        double totalHours,
        long sessions,
        long pomodoros,
        double avgDifficulty
) {
}
