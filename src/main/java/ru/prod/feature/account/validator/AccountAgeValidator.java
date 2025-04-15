package ru.prod.feature.account.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.Period;

public class AccountAgeValidator implements ConstraintValidator<ValidAccountAge, LocalDate> {

    private int minAge;
    private int maxAge;

    @Override
    public void initialize(ValidAccountAge constraintAnnotation) {
        this.minAge = constraintAnnotation.minAge();
        this.maxAge = constraintAnnotation.maxAge();
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        LocalDate today = LocalDate.now();
        int age = Period.between(value, today).getYears();

        return age >= minAge && age <= maxAge;
    }
}