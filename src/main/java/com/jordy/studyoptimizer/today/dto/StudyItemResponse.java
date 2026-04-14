package com.jordy.studyoptimizer.today.dto;

import com.jordy.studyoptimizer.today.StudyItemType;

/**
 * Una sugerencia concreta dentro del plan de hoy: que hacer, cuanto tiempo
 * estimado cuesta y por que se propone. Es un record (inmutable) para no
 * exponer entidades JPA al exterior.
 */
public record StudyItemResponse(
        StudyItemType type,
        String title,
        String detail,
        int estimatedMinutes,
        String reason
) {
}
