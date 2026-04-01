package com.jordy.studyoptimizer.exercise.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Alta de un reto. dayNumber, phase, estimatedMinutes y conceptIds son opcionales:
 * si no llega dayNumber, se asigna el siguiente numero disponible; si no llega
 * phase, arranca en fase 1.
 */
public record CreateExerciseRequest(
        @Min(value = 1, message = "el numero de reto debe ser mayor o igual a 1")
        Integer dayNumber,

        @NotBlank(message = "el titulo es obligatorio")
        @Size(max = 200, message = "el titulo no puede superar 200 caracteres")
        String title,

        String description,

        @Min(value = 1, message = "la fase minima es 1")
        @Max(value = 100, message = "la fase maxima es 100")
        Integer phase,

        @Min(value = 1, message = "el estimado debe ser al menos 1 minuto")
        @Max(value = 100_000, message = "el estimado no puede superar 100000 minutos")
        Integer estimatedMinutes,

        List<@NotNull Long> conceptIds
) {
}
