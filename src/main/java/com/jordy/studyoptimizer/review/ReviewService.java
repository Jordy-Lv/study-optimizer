package com.jordy.studyoptimizer.review;

import com.jordy.studyoptimizer.common.exception.ResourceNotFoundException;
import com.jordy.studyoptimizer.concept.Concept;
import com.jordy.studyoptimizer.concept.ConceptRepository;
import com.jordy.studyoptimizer.concept.ConceptService;
import com.jordy.studyoptimizer.review.dto.DueConceptResponse;
import com.jordy.studyoptimizer.review.dto.ReviewResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ReviewService {

    private final ReviewRepository repository;
    private final ConceptRepository conceptRepository;
    private final ConceptService conceptService;

    public ReviewService(ReviewRepository repository,
                         ConceptRepository conceptRepository,
                         ConceptService conceptService) {
        this.repository = repository;
        this.conceptRepository = conceptRepository;
        this.conceptService = conceptService;
    }

    /**
     * Registra un repaso de un concepto con su calidad (0-5) y aplica SM-2
     * para recalcular el factor de facilidad, las repeticiones y la fecha
     * del proximo repaso. Si el concepto aun no tenia ficha, se crea.
     */
    public ReviewResponse record(Long conceptId, int quality) {
        Concept concept = conceptService.findOrThrow(conceptId);
        ConceptReview review = repository.findByConcept_Id(conceptId)
                .orElseGet(() -> new ConceptReview(concept, LocalDate.now()));

        Sm2.Result r = Sm2.next(
                review.getEasiness(),
                review.getRepetitions(),
                review.getIntervalDays(),
                quality);

        review.setEasiness(r.easiness());
        review.setRepetitions(r.repetitions());
        review.setIntervalDays(r.intervalDays());
        review.setDueDate(LocalDate.now().plusDays(r.intervalDays()));
        review.setLastReviewedAt(LocalDateTime.now());
        review.setTotalReviews(review.getTotalReviews() + 1);

        return ReviewResponse.from(repository.save(review));
    }

    /**
     * Que repasar hoy: conceptos nuevos (sin ficha todavia) + fichas vencidas
     * o que vencen hoy. Los nuevos van primero.
     */
    @Transactional(readOnly = true)
    public List<DueConceptResponse> today() {
        LocalDate today = LocalDate.now();
        List<DueConceptResponse> result = new ArrayList<>();

        // Conceptos nuevos = los que aun no tienen ficha de repaso.
        List<Long> reviewed = repository.findReviewedConceptIds();
        for (Concept c : conceptRepository.findAllByOrderByNameAsc()) {
            if (!reviewed.contains(c.getId())) {
                result.add(new DueConceptResponse(
                        c.getId(), c.getName(), c.getCategory(), "NEW", today, 0));
            }
        }
        for (ConceptReview r : repository.findByDueDateLessThanEqualOrderByDueDateAsc(today)) {
            Concept c = r.getConcept();
            result.add(new DueConceptResponse(
                    c.getId(), c.getName(), c.getCategory(), "DUE",
                    r.getDueDate(), r.getIntervalDays()));
        }
        return result;
    }

    /** Estado actual de la ficha de un concepto (404 si nunca se repaso). */
    @Transactional(readOnly = true)
    public ReviewResponse getForConcept(Long conceptId) {
        conceptService.findOrThrow(conceptId);
        return repository.findByConcept_Id(conceptId)
                .map(ReviewResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "El concepto " + conceptId + " todavia no tiene repasos registrados"));
    }
}
