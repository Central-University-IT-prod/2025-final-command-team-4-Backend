package ru.prod.feature.account.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AccountPasswordValidator implements ConstraintValidator<ValidAccountPassword, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return value.matches("^(?=.*[0-9])(?=.*[a-zA-Z\\S]).{8,}$");
    }
}