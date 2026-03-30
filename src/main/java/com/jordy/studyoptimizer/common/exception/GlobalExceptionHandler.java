package com.jordy.studyoptimizer.common.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Captura excepciones lanzadas por cualquier controller y las traduce a
 * respuestas HTTP coherentes. Usamos ProblemDetail (RFC 7807), el formato
 * estandar de errores que trae Spring 6 / Spring Boot 3.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleNotFound(ResourceNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ProblemDetail handleDuplicate(DuplicateResourceException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    /**
     * Falla un servicio externo del que dependemos (GitHub): rate limit, caida,
     * timeout... Nuestra API esta bien, el tercero no -> 502 Bad Gateway.
     */
    @ExceptionHandler(ExternalServiceException.class)
    public ProblemDetail handleExternalService(ExternalServiceException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_GATEWAY, ex.getMessage());
    }

    /**
     * Se dispara cuando falla una validacion (@NotBlank, @Min, @Max...).
     * Juntamos todos los campos invalidos en un solo mensaje.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        String detalle = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detalle);
    }

    /**
     * Igual que la anterior pero para validaciones sobre query params / path
     * vars (@Min, @Max... en un controller @Validated): ahi salta esta, no
     * MethodArgumentNotValidException (que es solo para @RequestBody).
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException ex) {
        String detalle = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.joining("; "));
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detalle);
    }

    /**
     * Parametro con el tipo equivocado, p.ej. ?energy=SUPER cuando se espera un
     * enum. Si el tipo esperado es un enum, listamos los valores validos.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        Class<?> type = ex.getRequiredType();
        String detalle = (type != null && type.isEnum())
                ? "Valor invalido para '" + ex.getName() + "'. Validos: "
                        + Arrays.stream(type.getEnumConstants())
                                .map(Object::toString)
                                .collect(Collectors.joining(", "))
                : "Valor invalido para el parametro '" + ex.getName() + "'.";
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detalle);
    }
}
