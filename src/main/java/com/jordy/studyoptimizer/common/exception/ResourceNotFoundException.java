package com.jordy.studyoptimizer.common.exception;

/**
 * Se lanza cuando se busca un recurso por id y no existe.
 * El GlobalExceptionHandler la traduce a un HTTP 404.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException of(String entity, Object id) {
        return new ResourceNotFoundException(entity + " con id " + id + " no encontrado");
    }
}
