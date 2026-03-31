package com.jordy.studyoptimizer.exercise.dto;

import com.jordy.studyoptimizer.concept.dto.ConceptResponse;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Lo que la API devuelve sobre un reto. Es un record (inmutable) para no
 * exponer la entidad JPA directamente al exterior.
 */
public record ExerciseResponse(
        Long id,
        Integer dayNumber,
        String title,
        String description,
        Integer phase,
        boolean done,
        LocalDateTime completedAt,
        Integer estimatedMinutes,
        List<ConceptResponse> concepts
) {
}
