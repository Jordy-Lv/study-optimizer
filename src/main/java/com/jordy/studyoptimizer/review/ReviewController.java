package com.jordy.studyoptimizer.review;

import com.jordy.studyoptimizer.review.dto.DueConceptResponse;
import com.jordy.studyoptimizer.review.dto.RecordReviewRequest;
import com.jordy.studyoptimizer.review.dto.ReviewResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService service;

    public ReviewController(ReviewService service) {
        this.service = service;
    }

    /** Que conceptos toca repasar hoy. */
    @GetMapping("/today")
    public List<DueConceptResponse> today() {
        return service.today();
    }

    /** Registra un repaso de un concepto con su calidad (0-5). */
    @PostMapping("/concepts/{conceptId}")
    public ReviewResponse record(@PathVariable Long conceptId,
                                 @Valid @RequestBody RecordReviewRequest req) {
        return service.record(conceptId, req.quality());
    }

    /** Estado de la ficha de repaso de un concepto. */
    @GetMapping("/concepts/{conceptId}")
    public ReviewResponse forConcept(@PathVariable Long conceptId) {
        return service.getForConcept(conceptId);
    }
}
