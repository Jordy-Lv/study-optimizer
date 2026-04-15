package com.jordy.studyoptimizer.report;

import com.jordy.studyoptimizer.achievement.dto.AchievementResponse;
import com.jordy.studyoptimizer.analytics.dto.ExerciseStatsResponse;
import com.jordy.studyoptimizer.analytics.dto.SummaryResponse;
import com.jordy.studyoptimizer.goal.dto.GoalResponse;
import com.jordy.studyoptimizer.streak.dto.StreakResponse;

import java.time.LocalDate;
import java.util.List;

/**
 * Foto del progreso reunida desde varios dominios para volcarla a PDF.
 *
 * No es un DTO de respuesta JSON: es un objeto interno que viaja del
 * {@link ReportService} (que lo arma) al {@link ProgressPdfBuilder} (que lo
 * dibuja). Asi el constructor del PDF no necesita conocer ningun service.
 */
public record ProgressReportData(
        LocalDate generatedOn,
        SummaryResponse summary,
        StreakResponse streak,
        long completedExercises,
        long totalExercises,
        List<Integer> completedPhases,
        List<GoalResponse> goals,
        List<AchievementResponse> achievements,
        List<ExerciseStatsResponse> topExercises
) {
}
