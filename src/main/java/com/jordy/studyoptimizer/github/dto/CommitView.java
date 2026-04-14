package com.jordy.studyoptimizer.github.dto;

import java.time.OffsetDateTime;

/**
 * Vista simplificada de un commit para el endpoint de exploracion
 * (GET /api/github/commits). Aplana el JSON anidado de GitHub a lo que interesa
 * y anade {@code detectedDay}: el reto que detecto el CommitMatcher (null si el
 * mensaje no menciona ninguno). Sirve para PREVISUALIZAR que se sincronizaria
 * antes de tocar nada.
 */
public record CommitView(
        String sha,
        String message,
        String author,
        OffsetDateTime date,
        Integer detectedDay) {
}
