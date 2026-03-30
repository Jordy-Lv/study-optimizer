package com.jordy.studyoptimizer.common.exception;

/**
 * Se lanza cuando se intenta crear algo que viola una restriccion de unicidad
 * (por ejemplo, dos conceptos con el mismo nombre).
 * El GlobalExceptionHandler la traduce a un HTTP 409 (Conflict).
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
