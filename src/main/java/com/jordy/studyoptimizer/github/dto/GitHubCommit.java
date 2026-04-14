package com.jordy.studyoptimizer.github.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.OffsetDateTime;

/**
 * Mapeo (parcial) de un commit tal como lo devuelve la API de GitHub en
 * GET /repos/{owner}/{repo}/commits. Solo declaramos los campos que usamos; el
 * JSON real trae decenas mas.
 *
 * @JsonIgnoreProperties(ignoreUnknown = true): le dice a Jackson que ignore
 * cualquier campo del JSON que no este en el record, en vez de fallar. Es lo
 * correcto al consumir una API de terceros que puede anadir campos en cualquier
 * momento: no queremos acoplarnos a su forma completa.
 *
 * La estructura anidada (sha arriba; mensaje/autor dentro de "commit") refleja
 * literalmente el JSON de GitHub, por eso usamos records anidados.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record GitHubCommit(
        String sha,
        Commit commit) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Commit(
            String message,
            Author author) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Author(
            String name,
            OffsetDateTime date) {
    }

    /** Primeros 7 caracteres del sha, como muestra git en la consola. */
    public String shortSha() {
        return sha == null ? null : sha.substring(0, Math.min(7, sha.length()));
    }

    public String message() {
        return commit != null ? commit.message() : null;
    }

    public String authorName() {
        return commit != null && commit.author() != null ? commit.author().name() : null;
    }

    public OffsetDateTime date() {
        return commit != null && commit.author() != null ? commit.author().date() : null;
    }
}
