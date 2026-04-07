package com.jordy.studyoptimizer.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Calidad del recuerdo al repasar un concepto, escala SM-2:
 * 0 = no me acorde nada ... 5 = perfecto.
 */
public record RecordReviewRequest(
        @NotNull(message = "la calidad es obligatoria")
        @Min(value = 0, message = "la calidad minima es 0")
        @Max(value = 5, message = "la calidad maxima es 5")
        Integer quality
) {
}
