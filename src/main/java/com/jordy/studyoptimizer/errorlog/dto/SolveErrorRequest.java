package com.jordy.studyoptimizer.errorlog.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Registra la solucion de un error y lo marca como resuelto (rellena resolvedAt).
 */
public record SolveErrorRequest(
        @NotBlank String solution) {
}
