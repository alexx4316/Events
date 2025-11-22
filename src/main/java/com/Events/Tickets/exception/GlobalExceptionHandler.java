package com.Events.Tickets.exception;

import com.Events.Tickets.dto.response.ErrorResponseDTO;
import com.Events.Tickets.exception.DataConflictException;
import com.Events.Tickets.exception.ResourceNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

// @RestControllerAdvice centraliza el manejo de excepciones para todos los Controllers
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ------------------ 1. Manejo de 404 Not Found ------------------
    // Captura ResourceNotFoundException (lanzada por nuestros Services)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleResourceNotFound(
            ResourceNotFoundException ex, WebRequest request) {

        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(ex.getMessage()) // El mensaje claro que pusimos en el Service
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND); // Retorna 404
    }

    // ------------------ 2. Manejo de 409 Conflict ------------------
    // Captura DataConflictException (lanzada por unicidad de nombre de Evento)
    @ExceptionHandler(DataConflictException.class)
    public ResponseEntity<ErrorResponseDTO> handleDataConflict(
            DataConflictException ex, WebRequest request) {

        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.CONFLICT.value())
                .error(HttpStatus.CONFLICT.getReasonPhrase())
                .message(ex.getMessage()) // Mensaje sobre el nombre duplicado
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(error, HttpStatus.CONFLICT); // Retorna 409
    }

    // ------------------ 3. Manejo de 400 Bad Request (Validación) ------------------
    // Captura MethodArgumentNotValidException (lanzada por @Valid en Controllers)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationErrors(
            MethodArgumentNotValidException ex, WebRequest request) {

        // Mapea todos los errores de validación a un mapa más legible
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        // Formatea todos los errores en un solo mensaje para el cliente
        String message = "Field validation failure: " + errors.toString();

        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(message) // Mensaje detallado de los campos fallidos
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST); // Retorna 400
    }
}