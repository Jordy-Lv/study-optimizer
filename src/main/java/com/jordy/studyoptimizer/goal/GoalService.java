package com.jordy.studyoptimizer.goal;

import com.jordy.studyoptimizer.common.exception.DuplicateResourceException;
import com.jordy.studyoptimizer.common.exception.ResourceNotFoundException;
import com.jordy.studyoptimizer.exercise.ExerciseRepository;
import com.jordy.studyoptimizer.goal.dto.CreateGoalRequest;
import com.jordy.studyoptimizer.goal.dto.GoalResponse;
import com.jordy.studyoptimizer.goal.dto.UpdateGoalRequest;
import com.jordy.studyoptimizer.session.SessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Metas semanales: el usuario fija un objetivo (ej. 3 retos/semana) y aqui se
 * mide su avance. El avance NO se persiste: se calcula al leer sobre la semana
 * actual (lunes a domingo), para no tener estado duplicado que se desincronice.
 */
@Service
public class GoalService {

    private final GoalRepository goalRepository;
    private final ExerciseRepository exerciseRepository;
    private final SessionRepository sessionRepository;

    public GoalService(
            GoalRepository goalRepository,
            ExerciseRepository exerciseRepository,
            SessionRepository sessionRepository) {
        this.goalRepository = goalRepository;
        this.exerciseRepository = exerciseRepository;
        this.sessionRepository = sessionRepository;
    }

    @Transactional(readOnly = true)
    public List<GoalResponse> list() {
        return goalRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public GoalResponse get(Long id) {
        WeeklyGoal goal = goalRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Meta", id));
        return toResponse(goal);
    }

    @Transactional
    public GoalResponse create(CreateGoalRequest request) {
        if (goalRepository.existsByMetric(request.metric())) {
            throw new DuplicateResourceException(
                    "Ya existe una meta para la metrica " + request.metric());
        }
        WeeklyGoal goal = new WeeklyGoal();
        goal.setMetric(request.metric());
        goal.setTarget(request.target());
        return toResponse(goalRepository.save(goal));
    }

    @Transactional
    public GoalResponse update(Long id, UpdateGoalRequest request) {
        WeeklyGoal goal = goalRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Meta", id));
        goal.setTarget(request.target());
        return toResponse(goalRepository.save(goal));
    }

    @Transactional
    public void delete(Long id) {
        if (!goalRepository.existsById(id)) {
            throw ResourceNotFoundException.of("Meta", id);
        }
        goalRepository.deleteById(id);
    }

    private GoalResponse toResponse(WeeklyGoal goal) {
        LocalDate weekStart = LocalDate.now().with(DayOfWeek.MONDAY);
        LocalDate weekEnd = weekStart.plusDays(6);

        // Rango [lunes 00:00, domingo 23:59:59.999...] de la semana actual.
        LocalDateTime start = weekStart.atStartOfDay();
        LocalDateTime end = weekStart.plusDays(7).atStartOfDay().minusNanos(1);

        long current = measure(goal.getMetric(), start, end);
        int percent = (int) Math.min(100, current * 100 / goal.getTarget());
        boolean achieved = current >= goal.getTarget();

        return new GoalResponse(
                goal.getId(),
                goal.getMetric(),
                goal.getTarget(),
                current,
                percent,
                achieved,
                weekStart,
                weekEnd);
    }

    private long measure(GoalMetric metric, LocalDateTime start, LocalDateTime end) {
        return switch (metric) {
            case EXERCISES_COMPLETED ->
                    exerciseRepository.countByDoneTrueAndCompletedAtBetween(start, end);
            case SESSIONS ->
                    sessionRepository.countByStudiedAtBetween(start, end);
            case STUDY_MINUTES ->
                    sessionRepository.sumMinutesBetween(start, end);
        };
    }
}
