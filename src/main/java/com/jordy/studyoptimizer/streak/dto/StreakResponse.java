package com.jordy.studyoptimizer.streak.dto;

import java.time.LocalDate;

/**
 * @param currentStreak  dias seguidos estudiando hasta hoy (o ayer, con gracia de 1 dia)
 * @param longestStreak  la racha mas larga de la historia
 * @param totalStudyDays cuantos dias distintos has estudiado en total
 * @param lastStudyDate  ultimo dia con al menos una sesion (null si no hay datos)
 * @param studiedToday   si ya estudiaste hoy
 */
public record StreakResponse(
        int currentStreak,
        int longestStreak,
        long totalStudyDays,
        LocalDate lastStudyDate,
        boolean studiedToday
) {
}
