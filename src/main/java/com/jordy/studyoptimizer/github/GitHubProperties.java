package com.jordy.studyoptimizer.github;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuracion externa del modulo GitHub, leida del bloque "github" de
 * application.yml. Un record con @ConfigurationProperties es la forma idiomatica
 * en Spring Boot de tipar la configuracion: en vez de inyectar @Value sueltos,
 * agrupamos todo en un objeto inmutable que Spring rellena al arrancar.
 *
 * Por que externa y no hardcodeada: el repo a vigilar cambia segun el usuario, y
 * el token es un secreto -> nunca va en el codigo, se inyecta por variable de
 * entorno (GITHUB_TOKEN) con un default vacio para desarrollo.
 *
 * @param apiUrl base de la API REST de GitHub (se separa para poder apuntar a un
 *               mock en tests si hiciera falta).
 * @param owner  dueno del repositorio por defecto (usuario u organizacion).
 * @param repo   nombre del repositorio por defecto donde estan los commits.
 * @param token  token personal opcional. Sin el, GitHub limita a 60 peticiones/h
 *               y solo permite repos publicos; con el, 5000/h y repos privados.
 */
@ConfigurationProperties(prefix = "github")
public record GitHubProperties(
        String apiUrl,
        String owner,
        String repo,
        String token) {

    /** True si hay token configurado (no nulo ni en blanco). */
    public boolean hasToken() {
        return token != null && !token.isBlank();
    }
}
