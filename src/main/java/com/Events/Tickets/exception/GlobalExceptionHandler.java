package com.Events.Tickets.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.net.URI;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

// @RestControllerAdvice centraliza el manejo de excepciones para todos los Controllers
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ----------------------------------------------------
    // METODO AUXILIAR: Generación ProblemDetail, TraceId y Logging
    // ----------------------------------------------------
    private ProblemDetail createBaseProblemDetail(HttpStatus status, String title, String detail, WebRequest request) {
        String traceId = UUID.randomUUID().toString();

        // ProblemDetail básico con status y detail
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);

        // Propiedad 'instance' (similar a tu 'path')
        problemDetail.setInstance(URI.create(request.getDescription(false).replace("uri=", "")));

        // Inclusión de timestamp y traceId
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("traceId", traceId);

        // Esto permite buscar el error en los logs usando el ID devuelto al cliente.
        log.error("Error handled [TraceId: {}] - Status {}: {}", traceId, status.value(), detail);

        return problemDetail;
    }

    // ------------------ 1. Manejo de 404 Not Found ------------------
    // Captura ResourceNotFoundException (lanzada por nuestros Services)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFound(ResourceNotFoundException ex, WebRequest request) {

        ProblemDetail pd = createBaseProblemDetail(
                HttpStatus.NOT_FOUND,
                "Resource Not Found",
                ex.getMessage(),
                request
        );
        pd.setType(URI.create("/errors/not-found"));
        return pd;
    }

    // ------------------ 2. Manejo de 409 Conflict ------------------
    // Captura DataConflictException (lanzada por unicidad de nombre de Evento)
    @ExceptionHandler({DataConflictException.class, DataIntegrityViolationException.class})
    public ProblemDetail handleDataConflict(DataConflictException ex, WebRequest request) {

        // Usamos el mensaje de la excepción custom si está disponible
        String detail = (ex instanceof DataConflictException)
                ? ex.getMessage()
                : "Data integrity violation (for example, duplicate name or invalid foreign key).";

        ProblemDetail pd = createBaseProblemDetail(
                HttpStatus.CONFLICT,
                "Data Conflict",
                detail,
                request
        );
        pd.setType(URI.create("/errors/data-conflict"));
        return pd;
    }

    // ------------------ 3. Manejo de 400 Bad Request (Validación) ------------------
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationError (MethodArgumentNotValidException ex, WebRequest request) {

        ProblemDetail pd = createBaseProblemDetail(
                HttpStatus.BAD_REQUEST,
                "Validation Failed",
                "Uno o más campos de entrada no son válidos.",
                request
        );

        // Mapea los errores de validación
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage
                ));

        // Incluir el mapa de errores en el ProblemDetail (propiedad personalizada)
        pd.setType(URI.create("/errors/validation-failure"));
        pd.setProperty("errors", errors);

        return pd;
    }

    // ------------------ 4. Manejo de 500 Internal Server Error (Genérico) ------------------
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleInternalServerError(Exception ex, WebRequest request) {
        // Logging con el stack trace completo para errores 500
        log.error("Unhandled Internal Server Error: ", ex);

        ProblemDetail pd = createBaseProblemDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "An unexpected error has occurred on the server.",
                request
        );
        pd.setType(URI.create("/errors/internal-server"));
        return pd;
    }
}