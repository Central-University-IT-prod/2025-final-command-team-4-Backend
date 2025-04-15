package ru.prod.common.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.*;

@ControllerAdvice
public class ValidationExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(Exception ex) {
        List<Map<String, String>> errors = new ArrayList<>();

        if (ex instanceof MethodArgumentNotValidException) {
            errors = ((MethodArgumentNotValidException) ex).getBindingResult().getFieldErrors().stream()
                    .map(error -> Map.of(
                            "field", toSnakeCase(error.getField()),
                            "message", Objects.requireNonNull(error.getDefaultMessage())
                    ))
                    .sorted(Comparator.comparing(e -> e.get("field")))
                    .toList();
        } else if (ex instanceof ConstraintViolationException) {
            errors = ((ConstraintViolationException) ex).getConstraintViolations().stream()
                    .map(violation -> Map.of(
                            "field", toSnakeCase(violation.getPropertyPath().toString()),
                            "message", violation.getMessage()
                    ))
                    .sorted(Comparator.comparing(e -> e.get("field")))
                    .toList();
        }

        Map<String, Object> response = Map.of(
                "status", HttpStatus.BAD_REQUEST.value(),
                "errors", errors
        );

        return ResponseEntity.badRequest().body(response);
    }

    private String toSnakeCase(String original) {
        return original
                .replaceAll("([a-z])([A-Z]+)", "$1_$2")
                .toLowerCase();
    }
}
