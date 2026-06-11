package com.jordy.studyoptimizer.session.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Datos para registrar una sesion de estudio.
 * exerciseId, conceptIds y studiedAt son opcionales.
 */
public record CreateSessionRequest(
        Long exerciseId,

        @NotNull(message = "los minutos son obligatorios")
        @Min(value = 1, message = "la sesion debe durar al menos 1 minuto")
        Integer minutes,

        @Min(value = 1, message = "la dificultad minima es 1")
        @Max(value = 5, message = "la dificultad maxima es 5")
        Integer difficulty,

        String notes,

        List<Long> conceptIds,

        LocalDateTime studiedAt
) {
}
