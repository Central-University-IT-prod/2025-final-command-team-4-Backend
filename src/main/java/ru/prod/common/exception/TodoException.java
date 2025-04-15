package ru.prod.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class TodoException extends RuntimeException {

    private final HttpStatus status;

    private final Object mockData;

    public <T> TodoException(HttpStatus status, T mockData) {
        this.status = status;
        this.mockData = mockData;
    }
}
