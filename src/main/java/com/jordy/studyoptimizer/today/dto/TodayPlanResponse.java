package com.jordy.studyoptimizer.today.dto;

import com.jordy.studyoptimizer.today.Energy;

import java.util.List;

/**
 * El plan de estudio de hoy: el tiempo que dijo tener el usuario, su energia,
 * cuanto suman las actividades elegidas (plannedMinutes), la lista ordenada de
 * sugerencias y notas de contexto. Todo se calcula al vuelo; no se guarda nada.
 */
public record TodayPlanResponse(
        int availableMinutes,
        Energy energy,
        int plannedMinutes,
        List<StudyItemResponse> items,
        List<String> notes
) {
}
