package com.jordy.studyoptimizer.goal.dto;

import com.jordy.studyoptimizer.goal.GoalMetric;

import java.time.LocalDate;

/**
 * Meta + su avance en la semana actual. El avance (current/percent/achieved) NO
 * se guarda: se calcula al leer para no tener estado duplicado que se desincronice.
 */
public record GoalResponse(
        Long id,
        GoalMetric metric,
        int target,
        long current,
        int percent,
        boolean achieved,
        LocalDate weekStart,
        LocalDate weekEnd) {
}
