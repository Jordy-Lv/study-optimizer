package com.jordy.studyoptimizer.analytics.projection;

/**
 * Proyeccion de Spring Data: una "vista" de solo lectura del resultado de una
 * query de agregacion. Spring crea en tiempo de ejecucion un objeto que
 * implementa esta interfaz, emparejando cada getter con una columna del
 * resultado (por nombre, sin distinguir mayusculas ni guiones bajos).
 *
 * No es una entidad: no hay tabla detras, solo el resultado del SELECT.
 */
public interface SummaryProjection {
    Long getTotalMinutes();
    Long getTotalSessions();
    Long getDistinctDays();
    Double getAvgDifficulty();
}
