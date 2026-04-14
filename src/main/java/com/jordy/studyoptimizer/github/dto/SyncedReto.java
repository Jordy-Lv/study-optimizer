package com.jordy.studyoptimizer.github.dto;

/**
 * Resultado de un reto concreto detectado durante la sincronizacion.
 *
 * @param dayNumber numero de reto detectado en el commit.
 * @param title     titulo del reto si existe en la BD; null si era NO_EXERCISE.
 * @param status    que paso (ver {@link SyncStatus}).
 * @param commitSha sha corto del commit que disparo la deteccion.
 */
public record SyncedReto(
        int dayNumber,
        String title,
        SyncStatus status,
        String commitSha) {
}
