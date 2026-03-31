package com.jordy.studyoptimizer.exercise;

import com.jordy.studyoptimizer.common.exception.ResourceNotFoundException;
import com.jordy.studyoptimizer.concept.Concept;
import com.jordy.studyoptimizer.concept.ConceptService;
import com.jordy.studyoptimizer.concept.dto.ConceptResponse;
import com.jordy.studyoptimizer.exercise.dto.ExerciseResponse;
import com.jordy.studyoptimizer.exercise.dto.TimeComparisonResponse;
import com.jordy.studyoptimizer.session.ExerciseMinutesView;
import com.jordy.studyoptimizer.session.SessionRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class ExerciseService {

    private final ExerciseRepository repository;
    private final ConceptService conceptService;
    private final SessionRepository sessionRepository;

    public ExerciseService(ExerciseRepository repository,
                           ConceptService conceptService,
                           SessionRepository sessionRepository) {
        this.repository = repository;
        this.conceptService = conceptService;
        this.sessionRepository = sessionRepository;
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

    /** Marca un reto como hecho (idempotente: si ya estaba, conserva la fecha). */
    public ExerciseResponse markDone(Long id) {
        Exercise e = findOrThrow(id);
        if (!e.isDone()) {
            e.setDone(true);
            e.setCompletedAt(LocalDateTime.now());
        }
        return toResponse(e);
    }

    /** Asocia conceptos a un reto (escribe en la tabla puente exercise_concept). */
    public ExerciseResponse attachConcepts(Long id, List<Long> conceptIds) {
        Exercise e = findOrThrow(id);
        for (Long conceptId : conceptIds) {
            Concept c = conceptService.findOrThrow(conceptId);
            e.getConcepts().add(c);
        }
        return toResponse(e);
    }

    /** Quita un concepto de un reto. */
    public ExerciseResponse detachConcept(Long id, Long conceptId) {
        Exercise e = findOrThrow(id);
        e.getConcepts().removeIf(c -> c.getId().equals(conceptId));
        return toResponse(e);
    }

    @Transactional(readOnly = true)
    public Exercise findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Reto", id));
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
                e.getEstimatedMinutes(),
                concepts
        );
    }

    // --- Milestone 8: estimado vs real -------------------------------------

    /** Fija (o actualiza) el tiempo estimado de un reto, en minutos. */
    public ExerciseResponse setEstimate(Long id, Integer estimatedMinutes) {
        Exercise e = findOrThrow(id);
        e.setEstimatedMinutes(estimatedMinutes);
        return toResponse(e);
    }

    /** Estimado vs real de un reto. El real es la suma de minutos de sus sesiones. */
    @Transactional(readOnly = true)
    public TimeComparisonResponse timeComparison(Long id) {
        Exercise e = findOrThrow(id);
        long actual = sessionRepository.sumMinutesByExerciseId(id);
        return buildComparison(e, actual);
    }

    /**
     * Comparacion de todos los retos que ya tienen estimacion. Una sola consulta
     * agrupada trae los minutos reales por reto (evita N+1) y se cruzan en memoria.
     */
    @Transactional(readOnly = true)
    public List<TimeComparisonResponse> timeComparisons() {
        Map<Long, Long> realByExercise = sessionRepository.sumMinutesGroupedByExercise().stream()
                .collect(Collectors.toMap(ExerciseMinutesView::getExerciseId, ExerciseMinutesView::getMinutes));
        return repository.findAll(Sort.by("dayNumber")).stream()
                .filter(e -> e.getEstimatedMinutes() != null)
                .map(e -> buildComparison(e, realByExercise.getOrDefault(e.getId(), 0L)))
                .toList();
    }

    /** Calcula diferencia y desviacion (no se persisten: son metricas derivadas). */
    private static TimeComparisonResponse buildComparison(Exercise e, long actualMinutes) {
        Integer estimated = e.getEstimatedMinutes();
        Integer difference = null;
        Integer deviationPercent = null;
        if (estimated != null) {
            difference = (int) (actualMinutes - estimated);
            if (estimated != 0) {
                deviationPercent = (int) Math.round((actualMinutes - estimated) * 100.0 / estimated);
            }
        }
        return new TimeComparisonResponse(
                e.getId(),
                e.getDayNumber(),
                e.getTitle(),
                estimated,
                actualMinutes,
                difference,
                deviationPercent
        );
    }
}
