package com.jordy.studyoptimizer.report;

import com.jordy.studyoptimizer.achievement.AchievementService;
import com.jordy.studyoptimizer.analytics.AnalyticsService;
import com.jordy.studyoptimizer.analytics.dto.ExerciseStatsResponse;
import com.jordy.studyoptimizer.exercise.ExerciseRepository;
import com.jordy.studyoptimizer.goal.GoalService;
import com.jordy.studyoptimizer.streak.StreakService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

/**
 * Dominio "de alto nivel": no tiene tabla propia, compone las metricas que ya
 * calculan otros services (analytics, racha, logros, metas, retos) en una sola
 * foto y delega el dibujo del PDF en {@link ProgressPdfBuilder}.
 *
 * Mismo patron que AchievementService: reutilizamos las metricas existentes en
 * vez de recalcularlas, para que el reporte nunca se desincronice de la API.
 */
@Service
@Transactional(readOnly = true)
public class ReportService {

    /** Cuantos retos mostrar en la tabla "con mas tiempo invertido". */
    private static final int TOP_EXERCISES = 5;

    private final AnalyticsService analyticsService;
    private final StreakService streakService;
    private final AchievementService achievementService;
    private final GoalService goalService;
    private final ExerciseRepository exerciseRepository;
    private final ProgressPdfBuilder pdfBuilder;

    public ReportService(AnalyticsService analyticsService,
                         StreakService streakService,
                         AchievementService achievementService,
                         GoalService goalService,
                         ExerciseRepository exerciseRepository,
                         ProgressPdfBuilder pdfBuilder) {
        this.analyticsService = analyticsService;
        this.streakService = streakService;
        this.achievementService = achievementService;
        this.goalService = goalService;
        this.exerciseRepository = exerciseRepository;
        this.pdfBuilder = pdfBuilder;
    }

    /** Reune los datos del progreso y devuelve el PDF ya renderizado. */
    public byte[] progressPdf() {
        return pdfBuilder.build(gather());
    }

    private ProgressReportData gather() {
        List<ExerciseStatsResponse> topExercises = analyticsService.byExercise().stream()
                .sorted(Comparator.comparingLong(ExerciseStatsResponse::totalMinutes).reversed())
                .limit(TOP_EXERCISES)
                .toList();

        return new ProgressReportData(
                LocalDate.now(),
                analyticsService.summary(),
                streakService.compute(),
                exerciseRepository.countByDone(true),
                exerciseRepository.count(),
                exerciseRepository.findCompletedPhases().stream().sorted().toList(),
                goalService.list(),
                achievementService.list(),
                topExercises
        );
    }
}
