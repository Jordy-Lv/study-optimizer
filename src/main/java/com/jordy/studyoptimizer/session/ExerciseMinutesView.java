package com.jordy.studyoptimizer.session;

/**
 * Proyeccion de interfaz de Spring Data: total de minutos reales por reto.
 * La consulta agrupada de SessionRepository la rellena (los nombres de los
 * getters cuadran con los alias del JPQL: exerciseId, minutes).
 */
public interface ExerciseMinutesView {
    Long getExerciseId();

    Long getMinutes();
}
