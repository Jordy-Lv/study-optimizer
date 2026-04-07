package com.jordy.studyoptimizer.review.dto;

import com.jordy.studyoptimizer.review.ConceptReview;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Estado de la ficha de repaso de un concepto. */
public record ReviewResponse(
        Long conceptId,
        String conceptName,
        double easiness,
        int repetitions,
        int intervalDays,
        LocalDate dueDate,
        LocalDateTime lastReviewedAt,
        int totalReviews
) {
    public static ReviewResponse from(ConceptReview r) {
        return new ReviewResponse(
                r.getConcept().getId(),
                r.getConcept().getName(),
                r.getEasiness(),
                r.getRepetitions(),
                r.getIntervalDays(),
                r.getDueDate(),
                r.getLastReviewedAt(),
                r.getTotalReviews()
        );
    }
}
