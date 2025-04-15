package ru.prod.feature.account.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AccountAlreadyExistsException extends ApiException {

    public AccountAlreadyExistsException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
