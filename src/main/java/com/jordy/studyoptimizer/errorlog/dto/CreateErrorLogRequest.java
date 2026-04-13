package com.jordy.studyoptimizer.errorlog.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Alta de un error. exerciseId y solution son opcionales: se puede registrar
 * un error ya resuelto (con solucion) o uno todavia abierto (sin ella).
 */
public record CreateErrorLogRequest(
        @NotBlank String title,
        @NotBlank String description,
        Long exerciseId,
        String solution) {
}
