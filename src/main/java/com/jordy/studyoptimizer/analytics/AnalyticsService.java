package com.jordy.studyoptimizer.analytics;

import com.jordy.studyoptimizer.analytics.dto.ExerciseStatsResponse;
import com.jordy.studyoptimizer.analytics.dto.SummaryResponse;
import com.jordy.studyoptimizer.analytics.dto.WeeklyStatsResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AnalyticsService {

    /** Un pomodoro = 25 minutos de estudio enfocado. */
    private static final int POMODORO_MINUTES = 25;

    private final AnalyticsRepository repository;

    public AnalyticsService(AnalyticsRepository repository) {
        this.repository = repository;
    }

    public SummaryResponse summary() {
        var p = repository.summary();
        long minutes = nz(p.getTotalMinutes());
        return new SummaryResponse(
                minutes,
                toHours(minutes),
                nz(p.getTotalSessions()),
                nz(p.getDistinctDays()),
                toPomodoros(minutes),
                round1(p.getAvgDifficulty())
        );
    }

    public List<WeeklyStatsResponse> weekly() {
        return repository.weekly().stream()
                .map(p -> {
                    long minutes = nz(p.getTotalMinutes());
                    return new WeeklyStatsResponse(
                            p.getWeekStart(),
                            minutes,
                            toHours(minutes),
                            nz(p.getSessions()),
                            toPomodoros(minutes),
                            round1(p.getAvgDifficulty()));
                })
                .toList();
    }

    public List<ExerciseStatsResponse> byExercise() {
        return repository.byExercise().stream()
                .map(p -> {
                    long minutes = nz(p.getTotalMinutes());
                    return new ExerciseStatsResponse(
                            p.getExerciseId(),
                            p.getDayNumber(),
                            p.getTitle(),
                            minutes,
                            toHours(minutes),
                            nz(p.getSessions()),
                            toPomodoros(minutes));
                })
                .toList();
    }

    // --- helpers ---

    private static long nz(Long v) {
        return v == null ? 0L : v;
    }

    private static long toPomodoros(long minutes) {
        return Math.round((double) minutes / POMODORO_MINUTES);
    }

    private static double toHours(long minutes) {
        return Math.round(minutes / 60.0 * 10) / 10.0; // 1 decimal
    }

    private static double round1(Double avg) {
        if (avg == null) {
            return 0.0;
        }
        return Math.round(avg * 10) / 10.0;
    }
}
