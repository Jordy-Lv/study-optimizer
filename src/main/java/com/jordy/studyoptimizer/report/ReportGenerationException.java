package com.jordy.studyoptimizer.report;

/**
 * Falla al construir el PDF (problema de E/S o de la libreria). Es un error del
 * servidor (500): no hay nada que el cliente pueda corregir reintentando con
 * otros datos, asi que no la traducimos en el GlobalExceptionHandler.
 */
public class ReportGenerationException extends RuntimeException {

    public ReportGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
