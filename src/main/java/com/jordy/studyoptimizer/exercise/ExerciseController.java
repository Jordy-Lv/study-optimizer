package com.jordy.studyoptimizer.exercise;

import com.jordy.studyoptimizer.concept.dto.AttachConceptsRequest;
import com.jordy.studyoptimizer.exercise.dto.CreateExerciseRequest;
import com.jordy.studyoptimizer.exercise.dto.ExerciseResponse;
import com.jordy.studyoptimizer.exercise.dto.SetEstimateRequest;
import com.jordy.studyoptimizer.exercise.dto.TimeComparisonResponse;
import com.jordy.studyoptimizer.exercise.dto.UpdateExerciseRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/exercises")
public class ExerciseController {

    private final ExerciseService service;

    public ExerciseController(ExerciseService service) {
        this.service = service;
    }

    /** Lista los retos; opcionalmente filtra por fase con ?phase=2 */
    @GetMapping
    public List<ExerciseResponse> list(@RequestParam(required = false) Integer phase) {
        return phase == null ? service.list() : service.byPhase(phase);
    }

    @GetMapping("/{id}")
    public ExerciseResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    public ResponseEntity<ExerciseResponse> create(@Valid @RequestBody CreateExerciseRequest req,
                                                   UriComponentsBuilder uri) {
        ExerciseResponse created = service.create(req);
        URI location = uri.path("/api/exercises/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ExerciseResponse update(@PathVariable Long id, @Valid @RequestBody UpdateExerciseRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @PatchMapping("/{id}/done")
    public ExerciseResponse markDone(@PathVariable Long id) {
        return service.markDone(id);
    }

    @PostMapping("/{id}/concepts")
    public ExerciseResponse attachConcepts(@PathVariable Long id,
                                           @Valid @RequestBody AttachConceptsRequest req) {
        return service.attachConcepts(id, req.conceptIds());
    }

    @DeleteMapping("/{id}/concepts/{conceptId}")
    public ExerciseResponse detachConcept(@PathVariable Long id, @PathVariable Long conceptId) {
        return service.detachConcept(id, conceptId);
    }

    // --- Milestone 8: estimado vs real -------------------------------------

    /** Fija (o actualiza) el tiempo estimado de un reto, en minutos. */
    @PatchMapping("/{id}/estimate")
    public ExerciseResponse setEstimate(@PathVariable Long id,
                                        @Valid @RequestBody SetEstimateRequest req) {
        return service.setEstimate(id, req.estimatedMinutes());
    }

    /** Estimado vs real de un solo reto (real = suma de minutos de sus sesiones). */
    @GetMapping("/{id}/time")
    public TimeComparisonResponse timeComparison(@PathVariable Long id) {
        return service.timeComparison(id);
    }

    /** Estimado vs real de todos los retos que ya tienen estimacion. */
    @GetMapping("/time")
    public List<TimeComparisonResponse> timeComparisons() {
        return service.timeComparisons();
    }
}
