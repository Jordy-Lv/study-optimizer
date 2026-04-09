package com.jordy.studyoptimizer.goal;

/**
 * Que mide una meta semanal. Persistido como STRING (nunca ORDINAL).
 */
public enum GoalMetric {
    EXERCISES_COMPLETED,  // retos marcados como DONE en la semana
    STUDY_MINUTES,        // minutos estudiados en la semana
    SESSIONS              // numero de sesiones de la semana
}
