package com.jordy.studyoptimizer.exercise.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Edicion de los datos principales de un reto. El estimado se mantiene en su
 * endpoint dedicado PATCH /api/exercises/{id}/estimate.
 */
public record UpdateExerciseRequest(
        @Min(value = 1, message = "el numero de reto debe ser mayor o igual a 1")
        Integer dayNumber,

        @NotBlank(message = "el titulo es obligatorio")
        @Size(max = 200, message = "el titulo no puede superar 200 caracteres")
        String title,

        String description,

        @Min(value = 1, message = "la fase minima es 1")
        @Max(value = 100, message = "la fase maxima es 100")
        Integer phase
) {
}
