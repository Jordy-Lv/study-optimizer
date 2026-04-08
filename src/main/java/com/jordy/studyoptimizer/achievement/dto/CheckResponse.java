package com.jordy.studyoptimizer.achievement.dto;

import java.util.List;

/**
 * Resultado de evaluar los logros: el estado completo y cuales se acaban de
 * desbloquear en esta evaluacion (lo que mostrarias como "¡Nuevo logro!").
 */
public record CheckResponse(
        List<AchievementResponse> newlyUnlocked,
        List<AchievementResponse> all
) {
}
