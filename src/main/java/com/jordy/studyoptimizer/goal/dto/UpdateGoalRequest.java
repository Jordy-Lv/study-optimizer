package com.jordy.studyoptimizer.goal.dto;

import jakarta.validation.constraints.Min;

public record UpdateGoalRequest(
        @Min(1) int target) {
}
