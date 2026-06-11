package com.jordy.studyoptimizer.exercise;

import com.jordy.studyoptimizer.common.exception.DuplicateResourceException;
import com.jordy.studyoptimizer.common.exception.ResourceNotFoundException;
import com.jordy.studyoptimizer.concept.Concept;
import com.jordy.studyoptimizer.concept.ConceptService;
import com.jordy.studyoptimizer.concept.dto.ConceptResponse;
import com.jordy.studyoptimizer.exercise.dto.CreateExerciseRequest;
import com.jordy.studyoptimizer.exercise.dto.ExerciseResponse;
import com.jordy.studyoptimizer.exercise.dto.UpdateExerciseRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ExerciseService {

    private final ExerciseRepository repository;
    private final ConceptService conceptService;

    public ExerciseService(ExerciseRepository repository,
                           ConceptService conceptService) {
        this.repository = repository;
        this.conceptService = conceptService;
    }

    @Transactional(readOnly = true)
    public List<ExerciseResponse> list() {
        return repository.findAll(org.springframework.data.domain.Sort.by("dayNumber")).stream()
                .map(ExerciseService::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ExerciseResponse get(Long id) {
        return toResponse(findOrThrow(id));
    }

    /** Crea un ejercicio propio del usuario. Los de ejemplo son solo seed inicial. */
    public ExerciseResponse create(CreateExerciseRequest req) {
        Integer dayNumber = req.dayNumber() == null ? nextDayNumber() : req.dayNumber();
        ensureDayNumberAvailable(dayNumber, null);

        Exercise e = new Exercise();
        e.setDayNumber(dayNumber);
        e.setTitle(req.title().trim());
        e.setDescription(req.description());
        e.setPhase(req.phase() == null ? 1 : req.phase());
        attachConceptEntities(e, req.conceptIds());

        return toResponse(repository.save(e));
    }

    /** Edita los datos principales de un ejercicio, sea del seed inicial o creado por el usuario. */
    public ExerciseResponse update(Long id, UpdateExerciseRequest req) {
        Exercise e = findOrThrow(id);
        Integer dayNumber = req.dayNumber() == null ? e.getDayNumber() : req.dayNumber();
        ensureDayNumberAvailable(dayNumber, id);

        e.setDayNumber(dayNumber);
        e.setTitle(req.title().trim());
        e.setDescription(req.description());
        e.setPhase(req.phase() == null ? e.getPhase() : req.phase());

        return toResponse(e);
    }

    public void delete(Long id) {
        Exercise e = findOrThrow(id);
        repository.delete(e);
    }

    @Transactional(readOnly = true)
    public List<ExerciseResponse> byPhase(Integer phase) {
        return repository.findByPhaseOrderByDayNumber(phase).stream()
                .map(ExerciseService::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ExerciseResponse> byConcept(Long conceptId) {
        conceptService.findOrThrow(conceptId); // 404 si el concepto no existe
        return repository.findByConcepts_IdOrderByDayNumber(conceptId).stream()
                .map(ExerciseService::toResponse)
                .toList();
    }

    /** Marca un ejercicio como hecho (idempotente: si ya estaba, conserva la fecha). */
    public ExerciseResponse markDone(Long id) {
        Exercise e = findOrThrow(id);
        if (!e.isDone()) {
            e.setDone(true);
            e.setCompletedAt(LocalDateTime.now());
        }
        return toResponse(e);
    }

    /** Asocia conceptos a un ejercicio (escribe en la tabla puente exercise_concept). */
    public ExerciseResponse attachConcepts(Long id, List<Long> conceptIds) {
        Exercise e = findOrThrow(id);
        attachConceptEntities(e, conceptIds);
        return toResponse(e);
    }

    /** Quita un concepto de un ejercicio. */
    public ExerciseResponse detachConcept(Long id, Long conceptId) {
        Exercise e = findOrThrow(id);
        e.getConcepts().removeIf(c -> c.getId().equals(conceptId));
        return toResponse(e);
    }

    @Transactional(readOnly = true)
    public Exercise findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Ejercicio", id));
    }

    private void attachConceptEntities(Exercise exercise, List<Long> conceptIds) {
        if (conceptIds == null) {
            return;
        }
        for (Long conceptId : conceptIds) {
            Concept c = conceptService.findOrThrow(conceptId);
            exercise.getConcepts().add(c);
        }
    }

    private Integer nextDayNumber() {
        return repository.findTopByOrderByDayNumberDesc()
                .map(Exercise::getDayNumber)
                .map(n -> n + 1)
                .orElse(1);
    }

    private void ensureDayNumberAvailable(Integer dayNumber, Long currentId) {
        repository.findByDayNumber(dayNumber)
                .filter(existing -> currentId == null || !existing.getId().equals(currentId))
                .ifPresent(existing -> {
                    throw new DuplicateResourceException("Ya existe un ejercicio con numero " + dayNumber);
                });
    }

    /** Mapea la entidad (con sus conceptos) al DTO de salida. */
    private static ExerciseResponse toResponse(Exercise e) {
        List<ConceptResponse> concepts = e.getConcepts().stream()
                .map(ConceptResponse::from)
                .toList();
        return new ExerciseResponse(
                e.getId(),
                e.getDayNumber(),
                e.getTitle(),
                e.getDescription(),
                e.getPhase(),
                e.isDone(),
                e.getCompletedAt(),
                concepts
        );
    }
}
