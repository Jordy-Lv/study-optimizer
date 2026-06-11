package com.jordy.studyoptimizer.errorlog.dto;

import java.time.LocalDateTime;

/**
 * Error + su estado. "resolved" se deriva de resolvedAt (no se guarda aparte).
 * Los datos del ejercicio se aplanan (id, numero, titulo) para no exponer la entidad
 * Exercise entera; son null si el error no esta ligado a ningun ejercicio.
 */
public record ErrorLogResponse(
        Long id,
        String title,
        String description,
        String solution,
        boolean resolved,
        Long exerciseId,
        Integer exerciseDayNumber,
        String exerciseTitle,
        LocalDateTime createdAt,
        LocalDateTime resolvedAt) {
}
