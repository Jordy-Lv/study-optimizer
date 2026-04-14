package com.jordy.studyoptimizer.today;

import com.jordy.studyoptimizer.errorlog.ErrorLog;
import com.jordy.studyoptimizer.errorlog.ErrorLogRepository;
import com.jordy.studyoptimizer.exercise.Exercise;
import com.jordy.studyoptimizer.exercise.ExerciseRepository;
import com.jordy.studyoptimizer.review.ReviewService;
import com.jordy.studyoptimizer.review.dto.DueConceptResponse;
import com.jordy.studyoptimizer.today.dto.StudyItemResponse;
import com.jordy.studyoptimizer.today.dto.TodayPlanResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * "Que estudiar hoy": arma un plan que CABE en el tiempo disponible y se adapta
 * a la energia del usuario. No persiste nada ni tiene entidad propia: es un
 * <b>servicio orquestador</b> de solo lectura que compone datos de tres dominios
 * ya existentes —repasos espaciados (SM-2), retos pendientes y errores abiertos—
 * y los prioriza con una heuristica antes de llenar el tiempo de forma voraz.
 */
@Service
@Transactional(readOnly = true)
public class TodayService {

    // Estimaciones de cuanto cuesta cada actividad (minutos). Si el reto ya
    // tiene estimatedMinutes (milestone 8), se usa ese dato; si no, se cae a una
    // heuristica por fase (mas avanzada = mas costosa).
    private static final int REVIEW_MINUTES = 5;
    private static final int ERROR_FIX_MINUTES = 15;
    private static final int EXERCISE_BASE_MINUTES = 25;
    private static final int EXERCISE_MINUTES_PER_PHASE = 15;
    private static final int EXERCISE_MAX_MINUTES = 90;

    // Cuantos retos pendientes ofrecer como candidatos (los siguientes por dia).
    private static final int MAX_EXERCISE_CANDIDATES = 3;

    private final ReviewService reviewService;
    private final ExerciseRepository exerciseRepository;
    private final ErrorLogRepository errorLogRepository;

    public TodayService(ReviewService reviewService,
                        ExerciseRepository exerciseRepository,
                        ErrorLogRepository errorLogRepository) {
        this.reviewService = reviewService;
        this.exerciseRepository = exerciseRepository;
        this.errorLogRepository = errorLogRepository;
    }

    /**
     * Construye el plan de hoy: reune candidatos de los tres dominios, los ordena
     * por prioridad (segun energia) y los va metiendo mientras quepan en el tiempo.
     */
    public TodayPlanResponse plan(int availableMinutes, Energy energy) {
        List<Candidate> candidates = new ArrayList<>();
        candidates.addAll(reviewCandidates(energy));
        candidates.addAll(exerciseCandidates(energy));
        candidates.addAll(errorCandidates(energy));

        // Mayor score primero. El sort es estable: ante empates se conserva el
        // orden de insercion (repasos mas vencidos y retos mas tempranos van antes).
        candidates.sort(Comparator.comparingInt(Candidate::score).reversed());

        // Llenado voraz por tiempo: si un item no cabe lo saltamos pero seguimos,
        // porque uno mas corto que venga despues si podria entrar.
        List<StudyItemResponse> chosen = new ArrayList<>();
        int planned = 0;
        for (Candidate c : candidates) {
            int cost = c.item().estimatedMinutes();
            if (planned + cost <= availableMinutes) {
                chosen.add(c.item());
                planned += cost;
            }
        }

        return new TodayPlanResponse(
                availableMinutes,
                energy,
                planned,
                chosen,
                buildNotes(availableMinutes, energy, candidates, chosen, planned));
    }

    // --- candidatos por dominio ---

    /** Conceptos que tocan hoy (nuevos o vencidos), via el dominio de repasos. */
    private List<Candidate> reviewCandidates(Energy energy) {
        List<Candidate> out = new ArrayList<>();
        for (DueConceptResponse d : reviewService.today()) {
            boolean isNew = "NEW".equals(d.status());
            // Los vencidos pesan un poco mas que los nuevos (la curva de olvido).
            int score = baseScore(StudyItemType.REVIEW, energy) + (isNew ? 0 : 10);
            String detail = isNew
                    ? "Concepto nuevo (categoria " + d.category() + "), aun sin repasar."
                    : "Vencido (intervalo " + d.intervalDays() + " dias, vencia el " + d.dueDate() + ").";
            out.add(new Candidate(score, new StudyItemResponse(
                    StudyItemType.REVIEW,
                    "Repasar: " + d.conceptName(),
                    detail,
                    REVIEW_MINUTES,
                    "El repaso espaciado pierde efecto si se pospone.")));
        }
        return out;
    }

    /** Los siguientes retos pendientes (sin completar), ordenados por dia. */
    private List<Candidate> exerciseCandidates(Energy energy) {
        List<Candidate> out = new ArrayList<>();
        List<Exercise> pending = exerciseRepository.findByDoneOrderByDayNumber(false);
        int limit = Math.min(MAX_EXERCISE_CANDIDATES, pending.size());
        for (int i = 0; i < limit; i++) {
            Exercise e = pending.get(i);
            // El reto mas inmediato pesa mas que los siguientes (pequena penalizacion por orden).
            int score = baseScore(StudyItemType.EXERCISE, energy) - i * 5;
            int estimatedMinutes = estimateExerciseMinutes(e);
            out.add(new Candidate(score, new StudyItemResponse(
                    StudyItemType.EXERCISE,
                    "Reto " + e.getDayNumber() + ": " + e.getTitle(),
                    exerciseDetail(e),
                    estimatedMinutes,
                    exerciseReason(energy))));
        }
        return out;
    }

    /** Errores aun sin resolver (resolvedAt nulo), via el banco de errores. */
    private List<Candidate> errorCandidates(Energy energy) {
        List<Candidate> out = new ArrayList<>();
        // q="" + resolved=false → todos los errores abiertos (ver ErrorLogRepository).
        for (ErrorLog err : errorLogRepository.search("", null, false)) {
            out.add(new Candidate(baseScore(StudyItemType.ERROR_FIX, energy), new StudyItemResponse(
                    StudyItemType.ERROR_FIX,
                    "Resolver error: " + err.getTitle(),
                    trim(err.getDescription(), 120),
                    ERROR_FIX_MINUTES,
                    "Cerrar errores abiertos consolida lo aprendido.")));
        }
        return out;
    }

    // --- heuristica de prioridad ---

    /**
     * Peso base de cada tipo de actividad segun la energia. La idea: con poca
     * energia priorizar tareas ligeras (repasos, revisar errores); con mucha,
     * aprovechar para lo mas exigente (un reto nuevo). Medio = equilibrado.
     */
    private static int baseScore(StudyItemType type, Energy energy) {
        return switch (type) {
            case REVIEW -> switch (energy) {
                case LOW -> 100;
                case MEDIUM -> 90;
                case HIGH -> 70;
            };
            case ERROR_FIX -> switch (energy) {
                case LOW -> 75;
                case MEDIUM -> 65;
                case HIGH -> 55;
            };
            case EXERCISE -> switch (energy) {
                case LOW -> 30;
                case MEDIUM -> 70;
                case HIGH -> 100;
            };
        };
    }

    private static int estimateExerciseMinutes(Exercise exercise) {
        if (exercise.getEstimatedMinutes() != null) {
            return exercise.getEstimatedMinutes();
        }
        return estimateExerciseMinutesByPhase(exercise.getPhase());
    }

    private static int estimateExerciseMinutesByPhase(Integer phase) {
        int p = (phase == null) ? 1 : phase;
        int est = EXERCISE_BASE_MINUTES + (p - 1) * EXERCISE_MINUTES_PER_PHASE;
        return Math.min(est, EXERCISE_MAX_MINUTES);
    }

    private static String exerciseDetail(Exercise exercise) {
        if (exercise.getEstimatedMinutes() != null) {
            return "Reto pendiente (fase " + exercise.getPhase() + "); usa tu estimacion guardada.";
        }
        return "Reto pendiente (fase " + exercise.getPhase() + "); sin estimacion guardada, se estima por fase.";
    }

    private static String exerciseReason(Energy energy) {
        return switch (energy) {
            case HIGH -> "Energia alta: ideal para encarar un reto nuevo.";
            case MEDIUM -> "Con energia media puedes avanzar un reto si sobra tiempo.";
            case LOW -> "Con poca energia, opcional: mejor dejarlo para tareas mas ligeras.";
        };
    }

    private static String trim(String text, int max) {
        if (text == null) {
            return "";
        }
        return text.length() <= max ? text : text.substring(0, max - 1) + "…";
    }

    /** Notas de contexto para que el plan se explique solo. */
    private static List<String> buildNotes(int available, Energy energy,
                                           List<Candidate> candidates,
                                           List<StudyItemResponse> chosen, int planned) {
        List<String> notes = new ArrayList<>();
        if (candidates.isEmpty()) {
            notes.add("Nada pendiente: sin repasos, retos ni errores abiertos. ¡Buen trabajo!");
            return notes;
        }
        if (chosen.isEmpty()) {
            notes.add("Con " + available + " min no entra ni la tarea mas corta; sube el tiempo disponible.");
            return notes;
        }
        int free = available - planned;
        if (free >= REVIEW_MINUTES) {
            notes.add("Te sobran ~" + free + " min: cabe otro repaso o tomar notas.");
        }
        switch (energy) {
            case LOW -> notes.add("Energia baja: el plan prioriza repasos y errores sobre retos nuevos.");
            case HIGH -> notes.add("Energia alta: el plan prioriza avanzar retos nuevos.");
            case MEDIUM -> { /* equilibrado, sin nota extra */ }
        }
        return notes;
    }

    /** Sugerencia + su score interno (solo se usa para ordenar; no se expone). */
    private record Candidate(int score, StudyItemResponse item) {
    }
}
