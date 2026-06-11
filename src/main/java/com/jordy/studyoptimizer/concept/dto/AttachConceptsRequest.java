package com.jordy.studyoptimizer.concept.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * Cuerpo para asociar uno o varios conceptos a un ejercicio o sesion.
 * Se reutiliza desde exercise y session.
 */
public record AttachConceptsRequest(
        @NotEmpty(message = "debes enviar al menos un conceptId")
        List<Long> conceptIds
) {
}
