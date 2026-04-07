package com.jordy.studyoptimizer.review.dto;

import java.time.LocalDate;

/**
 * Un concepto que toca repasar hoy.
 * status = NEW (nunca repasado) o DUE (vencido / vence hoy).
 */
public record DueConceptResponse(
        Long conceptId,
        String conceptName,
        String category,
        String status,
        LocalDate dueDate,
        int intervalDays
) {
}
