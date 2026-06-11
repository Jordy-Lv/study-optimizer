package com.jordy.studyoptimizer.errorlog;

import com.jordy.studyoptimizer.common.exception.ResourceNotFoundException;
import com.jordy.studyoptimizer.errorlog.dto.CreateErrorLogRequest;
import com.jordy.studyoptimizer.errorlog.dto.ErrorLogResponse;
import com.jordy.studyoptimizer.errorlog.dto.SolveErrorRequest;
import com.jordy.studyoptimizer.errorlog.dto.UpdateErrorLogRequest;
import com.jordy.studyoptimizer.exercise.Exercise;
import com.jordy.studyoptimizer.exercise.ExerciseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Banco de errores: registrar bugs/errores y su solucion, con busqueda.
 * El estado "resuelto" se deriva de resolvedAt, no se guarda como campo aparte.
 */
@Service
public class ErrorLogService {

    private final ErrorLogRepository repository;
    private final ExerciseRepository exerciseRepository;

    public ErrorLogService(ErrorLogRepository repository, ExerciseRepository exerciseRepository) {
        this.repository = repository;
        this.exerciseRepository = exerciseRepository;
    }

    @Transactional(readOnly = true)
    public List<ErrorLogResponse> search(String q, Long exerciseId, Boolean resolved) {
        // q vacio/blanco → "" (el LIKE '%%' devuelve todo). Nunca null: ver nota
        // en ErrorLogRepository sobre "function lower(bytea)" en Postgres.
        String term = (q == null) ? "" : q.trim();
        return repository.search(term, exerciseId, resolved).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ErrorLogResponse get(Long id) {
        return toResponse(find(id));
    }

    @Transactional
    public ErrorLogResponse create(CreateErrorLogRequest request) {
        ErrorLog error = new ErrorLog();
        error.setTitle(request.title());
        error.setDescription(request.description());
        error.setExercise(resolveExercise(request.exerciseId()));

        // Si llega con solucion ya escrita, nace resuelto.
        if (request.solution() != null && !request.solution().isBlank()) {
            error.setSolution(request.solution());
            error.setResolvedAt(LocalDateTime.now());
        }
        return toResponse(repository.save(error));
    }

    @Transactional
    public ErrorLogResponse update(Long id, UpdateErrorLogRequest request) {
        ErrorLog error = find(id);
        error.setTitle(request.title());
        error.setDescription(request.description());
        error.setExercise(resolveExercise(request.exerciseId()));
        return toResponse(repository.save(error));
    }

    /** Registra la solucion y marca el error como resuelto. */
    @Transactional
    public ErrorLogResponse solve(Long id, SolveErrorRequest request) {
        ErrorLog error = find(id);
        error.setSolution(request.solution());
        // Solo sella la fecha la primera vez; reescribir la solucion no la cambia.
        if (error.getResolvedAt() == null) {
            error.setResolvedAt(LocalDateTime.now());
        }
        return toResponse(repository.save(error));
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw ResourceNotFoundException.of("Error", id);
        }
        repository.deleteById(id);
    }

    private ErrorLog find(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Error", id));
    }

    /** Resuelve el ejercicio opcional; lanza 404 si se pasa un id que no existe. */
    private Exercise resolveExercise(Long exerciseId) {
        if (exerciseId == null) {
            return null;
        }
        return exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> ResourceNotFoundException.of("Reto", exerciseId));
    }

    private ErrorLogResponse toResponse(ErrorLog error) {
        Exercise ex = error.getExercise();
        return new ErrorLogResponse(
                error.getId(),
                error.getTitle(),
                error.getDescription(),
                error.getSolution(),
                error.getResolvedAt() != null,
                ex != null ? ex.getId() : null,
                ex != null ? ex.getDayNumber() : null,
                ex != null ? ex.getTitle() : null,
                error.getCreatedAt(),
                error.getResolvedAt());
    }
}
