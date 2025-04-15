package ru.prod.feature.coworking.exception;

import org.springframework.http.HttpStatus;
import ru.prod.feature.account.exception.ApiException;

public class CoworkingNotFoundException extends ApiException {
    public CoworkingNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
