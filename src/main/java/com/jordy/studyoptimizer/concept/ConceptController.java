package com.jordy.studyoptimizer.concept;

import com.jordy.studyoptimizer.concept.dto.ConceptResponse;
import com.jordy.studyoptimizer.concept.dto.CreateConceptRequest;
import com.jordy.studyoptimizer.concept.dto.UpdateConceptRequest;
import com.jordy.studyoptimizer.exercise.ExerciseService;
import com.jordy.studyoptimizer.exercise.dto.ExerciseResponse;
import com.jordy.studyoptimizer.session.SessionService;
import com.jordy.studyoptimizer.session.dto.SessionResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/concepts")
public class ConceptController {

    private final ConceptService conceptService;
    private final ExerciseService exerciseService;
    private final SessionService sessionService;

    public ConceptController(ConceptService conceptService,
                             ExerciseService exerciseService,
                             SessionService sessionService) {
        this.conceptService = conceptService;
        this.exerciseService = exerciseService;
        this.sessionService = sessionService;
    }

    @PostMapping
    public ResponseEntity<ConceptResponse> create(@Valid @RequestBody CreateConceptRequest req,
                                                  UriComponentsBuilder uri) {
        ConceptResponse created = conceptService.create(req);
        URI location = uri.path("/api/concepts/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public List<ConceptResponse> list() {
        return conceptService.list();
    }

    @GetMapping("/{id}")
    public ConceptResponse get(@PathVariable Long id) {
        return conceptService.get(id);
    }

    @PutMapping("/{id}")
    public ConceptResponse update(@PathVariable Long id, @Valid @RequestBody UpdateConceptRequest req) {
        return conceptService.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        conceptService.delete(id);
    }

    /** Que retos practican este concepto (recorre la relacion N:M). */
    @GetMapping("/{id}/exercises")
    public List<ExerciseResponse> exercises(@PathVariable Long id) {
        return exerciseService.byConcept(id);
    }

    /** Que sesiones repasaron este concepto. */
    @GetMapping("/{id}/sessions")
    public List<SessionResponse> sessions(@PathVariable Long id) {
        return sessionService.byConcept(id);
    }
}
