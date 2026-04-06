package com.jordy.studyoptimizer.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<ConceptReview, Long> {

    Optional<ConceptReview> findByConcept_Id(Long conceptId);

    // Fichas que ya tocan (vencidas o que vencen hoy).
    List<ConceptReview> findByDueDateLessThanEqualOrderByDueDateAsc(LocalDate date);

    /** Ids de conceptos que YA tienen ficha de repaso (proyeccion escalar). */
    @Query("select r.concept.id from ConceptReview r")
    List<Long> findReviewedConceptIds();
}
