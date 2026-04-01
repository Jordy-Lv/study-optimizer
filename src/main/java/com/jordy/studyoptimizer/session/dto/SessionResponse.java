package com.jordy.studyoptimizer.session.dto;

import com.jordy.studyoptimizer.concept.dto.ConceptResponse;

import java.time.LocalDateTime;
import java.util.List;

public record SessionResponse(
        Long id,
        Long exerciseId,
        String exerciseTitle,
        Integer minutes,
        Integer difficulty,
        String notes,
        LocalDateTime studiedAt,
        List<ConceptResponse> concepts
) {
}
