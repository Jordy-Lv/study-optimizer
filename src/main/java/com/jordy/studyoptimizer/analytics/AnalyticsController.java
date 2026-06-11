package com.jordy.studyoptimizer.analytics;

import com.jordy.studyoptimizer.analytics.dto.ExerciseStatsResponse;
import com.jordy.studyoptimizer.analytics.dto.SummaryResponse;
import com.jordy.studyoptimizer.analytics.dto.WeeklyStatsResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService service;

    public AnalyticsController(AnalyticsService service) {
        this.service = service;
    }

    /** Resumen global: minutos, horas, sesiones, dias distintos, pomodoros. */
    @GetMapping("/summary")
    public SummaryResponse summary() {
        return service.summary();
    }

    /** Tiempo de estudio agregado por semana (la semana empieza el lunes). */
    @GetMapping("/weekly")
    public List<WeeklyStatsResponse> weekly() {
        return service.weekly();
    }

    /** Tiempo de estudio agregado por ejercicio. */
    @GetMapping("/by-exercise")
    public List<ExerciseStatsResponse> byExercise() {
        return service.byExercise();
    }
}
