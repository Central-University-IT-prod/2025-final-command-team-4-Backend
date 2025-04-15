package ru.prod.feature.account.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AccountAgeValidator.class)
public @interface ValidAccountAge {

    String message() default "Invalid age";

    int minAge() default 16;

    int maxAge() default 100;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}