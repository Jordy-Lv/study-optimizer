package com.jordy.studyoptimizer.session;

import com.jordy.studyoptimizer.common.exception.ResourceNotFoundException;
import com.jordy.studyoptimizer.concept.Concept;
import com.jordy.studyoptimizer.concept.ConceptService;
import com.jordy.studyoptimizer.concept.dto.ConceptResponse;
import com.jordy.studyoptimizer.exercise.Exercise;
import com.jordy.studyoptimizer.exercise.ExerciseService;
import com.jordy.studyoptimizer.session.dto.CreateSessionRequest;
import com.jordy.studyoptimizer.session.dto.SessionResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SessionService {

    private final SessionRepository repository;
    private final ExerciseService exerciseService;
    private final ConceptService conceptService;

    public SessionService(SessionRepository repository,
                          ExerciseService exerciseService,
                          ConceptService conceptService) {
        this.repository = repository;
        this.exerciseService = exerciseService;
        this.conceptService = conceptService;
    }

    public SessionResponse create(CreateSessionRequest req) {
        StudySession s = new StudySession();
        s.setMinutes(req.minutes());
        s.setDifficulty(req.difficulty());
        s.setNotes(req.notes());
        if (req.studiedAt() != null) {
            s.setStudiedAt(req.studiedAt());
        }
        if (req.exerciseId() != null) {
            Exercise e = exerciseService.findOrThrow(req.exerciseId());
            s.setExercise(e);
        }
        if (req.conceptIds() != null) {
            for (Long conceptId : req.conceptIds()) {
                Concept c = conceptService.findOrThrow(conceptId);
                s.getConcepts().add(c);
            }
        }
        return toResponse(repository.save(s));
    }

    @Transactional(readOnly = true)
    public List<SessionResponse> list(Long exerciseId) {
        List<StudySession> sessions = (exerciseId == null)
                ? repository.findAllByOrderByStudiedAtDesc()
                : repository.findByExercise_IdOrderByStudiedAtDesc(exerciseId);
        return sessions.stream().map(SessionService::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public SessionResponse get(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<SessionResponse> byConcept(Long conceptId) {
        conceptService.findOrThrow(conceptId);
        return repository.findByConcepts_IdOrderByStudiedAtDesc(conceptId).stream()
                .map(SessionService::toResponse)
                .toList();
    }

    public SessionResponse attachConcepts(Long id, List<Long> conceptIds) {
        StudySession s = findOrThrow(id);
        for (Long conceptId : conceptIds) {
            s.getConcepts().add(conceptService.findOrThrow(conceptId));
        }
        return toResponse(s);
    }

    public SessionResponse detachConcept(Long id, Long conceptId) {
        StudySession s = findOrThrow(id);
        s.getConcepts().removeIf(c -> c.getId().equals(conceptId));
        return toResponse(s);
    }

    public void delete(Long id) {
        repository.delete(findOrThrow(id));
    }

    private StudySession findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Sesion", id));
    }

    private static SessionResponse toResponse(StudySession s) {
        List<ConceptResponse> concepts = s.getConcepts().stream()
                .map(ConceptResponse::from)
                .toList();
        Exercise e = s.getExercise();
        return new SessionResponse(
                s.getId(),
                e != null ? e.getId() : null,
                e != null ? e.getTitle() : null,
                s.getMinutes(),
                s.getDifficulty(),
                s.getNotes(),
                s.getStudiedAt(),
                concepts
        );
    }
}
