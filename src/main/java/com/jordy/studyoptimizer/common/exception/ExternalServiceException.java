package com.jordy.studyoptimizer.common.exception;

/**
 * Se lanza cuando un servicio externo del que dependemos (por ejemplo la API de
 * GitHub) falla o responde con un error que no es culpa del cliente: rate limit,
 * caida, timeout, token invalido...
 *
 * El GlobalExceptionHandler la traduce a un HTTP 502 (Bad Gateway): nuestra API
 * funciona, pero el "upstream" del que dependemos no respondio bien. Asi el
 * cliente distingue "te equivocaste tu" (4xx) de "fallo un tercero" (502).
 */
public class ExternalServiceException extends RuntimeException {

    public ExternalServiceException(String message) {
        super(message);
    }

    public ExternalServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
