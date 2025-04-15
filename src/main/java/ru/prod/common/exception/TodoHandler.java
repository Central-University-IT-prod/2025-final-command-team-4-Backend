package ru.prod.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class TodoHandler {

    @ExceptionHandler(TodoException.class)
    public ResponseEntity<?> handleValidationExceptions(TodoException todo) {
        return ResponseEntity.status(todo.getStatus()).body(todo.getMockData());
    }
}
