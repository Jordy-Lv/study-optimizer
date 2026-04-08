package com.jordy.studyoptimizer.achievement;

import com.jordy.studyoptimizer.achievement.dto.AchievementResponse;
import com.jordy.studyoptimizer.achievement.dto.CheckResponse;
import com.jordy.studyoptimizer.analytics.AnalyticsService;
import com.jordy.studyoptimizer.analytics.dto.SummaryResponse;
import com.jordy.studyoptimizer.exercise.ExerciseRepository;
import com.jordy.studyoptimizer.streak.StreakService;
import com.jordy.studyoptimizer.streak.dto.StreakResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Evalua las reglas de logros contra el progreso real del usuario y persiste
 * los que se desbloquean. Es un dominio "de alto nivel": compone metricas de
 * exercise, analytics y streak.
 */
@Service
@Transactional
public class AchievementService {

    private final AchievementRepository repository;
    private final ExerciseRepository exerciseRepository;
    private final AnalyticsService analyticsService;
    private final StreakService streakService;

    public AchievementService(AchievementRepository repository,
                              ExerciseRepository exerciseRepository,
                              AnalyticsService analyticsService,
                              StreakService streakService) {
        this.repository = repository;
        this.exerciseRepository = exerciseRepository;
        this.analyticsService = analyticsService;
        this.streakService = streakService;
    }

    /** Solo lectura: estado actual de todos los logros (no desbloquea nada). */
    @Transactional(readOnly = true)
    public List<AchievementResponse> list() {
        Map<AchievementCode, UnlockedAchievement> unlocked = unlockedByCode();
        List<AchievementResponse> result = new ArrayList<>();
        for (AchievementCode code : AchievementCode.values()) {
            result.add(toResponse(code, unlocked.get(code)));
        }
        return result;
    }

    /** Evalua las reglas; desbloquea (persiste) las recien cumplidas. */
    public CheckResponse check() {
        AchievementContext context = buildContext();
        Map<AchievementCode, UnlockedAchievement> unlocked = unlockedByCode();

        List<AchievementResponse> newly = new ArrayList<>();
        for (AchievementCode code : AchievementCode.values()) {
            boolean alreadyUnlocked = unlocked.containsKey(code);
            if (!alreadyUnlocked && code.isMet(context)) {
                UnlockedAchievement saved = repository.save(new UnlockedAchievement(code));
                unlocked.put(code, saved);
                newly.add(toResponse(code, saved));
            }
        }

        List<AchievementResponse> all = new ArrayList<>();
        for (AchievementCode code : AchievementCode.values()) {
            all.add(toResponse(code, unlocked.get(code)));
        }
        return new CheckResponse(newly, all);
    }

    /** Reune las metricas de varios dominios en un solo contexto. */
    private AchievementContext buildContext() {
        SummaryResponse summary = analyticsService.summary();
        StreakResponse streak = streakService.compute();
        long completedExercises = exerciseRepository.countByDone(true);
        long completedPhases = exerciseRepository.findCompletedPhases().size();

        return new AchievementContext(
                completedExercises,
                completedPhases,
                streak.currentStreak(),
                streak.longestStreak(),
                summary.pomodoros(),
                summary.totalMinutes(),
                summary.totalSessions(),
                summary.distinctDays()
        );
    }

    private Map<AchievementCode, UnlockedAchievement> unlockedByCode() {
        Map<AchievementCode, UnlockedAchievement> map = new HashMap<>();
        for (UnlockedAchievement u : repository.findAll()) {
            map.put(u.getCode(), u);
        }
        return map;
    }

    private static AchievementResponse toResponse(AchievementCode code, UnlockedAchievement u) {
        return new AchievementResponse(
                code.name(),
                code.title(),
                code.description(),
                u != null,
                u != null ? u.getUnlockedAt() : null
        );
    }
}
