package com.jordy.studyoptimizer.exercise.dto;

/**
 * Estimado vs real de un reto. Solo {@code estimatedMinutes} se persiste;
 * el resto se calcula al leer:
 *  - actualMinutes:    suma de los minutos de las sesiones del reto.
 *  - differenceMinutes: actual - estimado (positivo = tardaste mas de lo previsto).
 *  - deviationPercent:  desviacion en % respecto al estimado.
 * differenceMinutes y deviationPercent son null si el reto aun no tiene estimacion.
 */
public record TimeComparisonResponse(
        Long exerciseId,
        Integer dayNumber,
        String title,
        Integer estimatedMinutes,
        long actualMinutes,
        Integer differenceMinutes,
        Integer deviationPercent
) {
}
