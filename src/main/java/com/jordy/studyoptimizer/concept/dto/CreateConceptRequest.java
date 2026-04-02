package com.jordy.studyoptimizer.concept.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateConceptRequest(
        @NotBlank(message = "el nombre es obligatorio")
        @Size(max = 120, message = "el nombre no puede superar 120 caracteres")
        String name,

        String description,

        @Size(max = 80, message = "la categoria no puede superar 80 caracteres")
        String category
) {
}
