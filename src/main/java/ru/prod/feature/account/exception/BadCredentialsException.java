package ru.prod.feature.account.exception;

import org.springframework.http.HttpStatus;

public class BadCredentialsException extends ApiException {

    public BadCredentialsException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
