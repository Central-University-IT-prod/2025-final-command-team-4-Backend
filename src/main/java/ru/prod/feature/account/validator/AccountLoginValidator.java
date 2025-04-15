package ru.prod.feature.account.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AccountLoginValidator implements ConstraintValidator<ValidAccountLogin, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return value.matches("^[a-zA-Z]{2,}$") && !value.equals("@me");
    }
}