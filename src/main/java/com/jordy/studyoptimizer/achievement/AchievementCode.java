package com.jordy.studyoptimizer.achievement;

import java.util.function.Predicate;

/**
 * Catalogo de logros. Cada constante es una REGLA: lleva su texto y un
 * predicado que decide, dado el contexto del usuario, si esta cumplido.
 *
 * Tener las reglas como datos (un enum) en vez de una cadena de if/else hace
 * trivial anadir un logro nuevo: agregas una linea aqui y ya se evalua.
 */
public enum AchievementCode {

    FIRST_SESSION("Primera sesion", "Registraste tu primera sesion de estudio.",
            c -> c.totalSessions() >= 1),

    FIRST_EXERCISE("Primer reto", "Completaste tu primer reto.",
            c -> c.completedExercises() >= 1),

    HALF_WAY("A mitad de camino", "Completaste 15 de los 30 retos.",
            c -> c.completedExercises() >= 15),

    ALL_DONE("Reto cumplido", "Completaste los 30 retos.",
            c -> c.completedExercises() >= 30),

    PHASE_MASTER("Maestro de fase", "Completaste una fase entera.",
            c -> c.completedPhases() >= 1),

    STREAK_3("En racha", "3 dias seguidos estudiando.",
            c -> c.longestStreak() >= 3),

    STREAK_7("Semana perfecta", "7 dias seguidos sin fallar.",
            c -> c.longestStreak() >= 7),

    POMODORO_10("Tomatero", "Acumulaste 10 pomodoros.",
            c -> c.totalPomodoros() >= 10),

    POMODORO_50("Maraton de foco", "Acumulaste 50 pomodoros.",
            c -> c.totalPomodoros() >= 50),

    TEN_HOURS("Diez horas", "Acumulaste 600 minutos de estudio.",
            c -> c.totalMinutes() >= 600);

    private final String title;
    private final String description;
    private final Predicate<AchievementContext> rule;

    AchievementCode(String title, String description, Predicate<AchievementContext> rule) {
        this.title = title;
        this.description = description;
        this.rule = rule;
    }

    public String title() {
        return title;
    }

    public String description() {
        return description;
    }

    /** ¿Esta cumplido este logro con el contexto dado? */
    public boolean isMet(AchievementContext context) {
        return rule.test(context);
    }
}
