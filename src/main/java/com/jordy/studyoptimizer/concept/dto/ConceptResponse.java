package com.jordy.studyoptimizer.concept.dto;

import com.jordy.studyoptimizer.concept.Concept;

import java.time.LocalDateTime;

public record ConceptResponse(
        Long id,
        String name,
        String description,
        String category,
        LocalDateTime createdAt
) {
    /** Fabrica para convertir la entidad en su DTO de salida. */
    public static ConceptResponse from(Concept c) {
        return new ConceptResponse(
                c.getId(),
                c.getName(),
                c.getDescription(),
                c.getCategory(),
                c.getCreatedAt()
        );
    }
}
