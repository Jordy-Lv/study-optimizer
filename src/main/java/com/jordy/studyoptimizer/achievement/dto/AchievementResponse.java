package com.jordy.studyoptimizer.achievement.dto;

import java.time.LocalDateTime;

/** Un logro y su estado para el usuario. */
public record AchievementResponse(
        String code,
        String title,
        String description,
        boolean unlocked,
        LocalDateTime unlockedAt
) {
}
