package com.jordy.studyoptimizer.goal.dto;

import com.jordy.studyoptimizer.goal.GoalMetric;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateGoalRequest(
        @NotNull GoalMetric metric,
        @Min(1) int target) {
}
