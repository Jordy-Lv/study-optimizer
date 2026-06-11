package com.jordy.studyoptimizer.errorlog.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Edita los datos del error (titulo, descripcion, ejercicio asociado). La solucion
 * NO se toca aqui: se registra con el endpoint dedicado POST .../solution.
 */
public record UpdateErrorLogRequest(
        @NotBlank String title,
        @NotBlank String description,
        Long exerciseId) {
}
