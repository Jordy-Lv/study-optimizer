package com.jordy.studyoptimizer.exercise.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Cuerpo para fijar el tiempo estimado (en minutos) de un reto.
 * Se valida con anotaciones: debe venir, y estar entre 1 y 100000 minutos.
 */
public record SetEstimateRequest(
        @NotNull @Min(1) @Max(100_000) Integer estimatedMinutes
) {
}
