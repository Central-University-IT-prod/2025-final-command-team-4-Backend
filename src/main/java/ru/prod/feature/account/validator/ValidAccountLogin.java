package ru.prod.feature.account.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AccountLoginValidator.class)
public @interface ValidAccountLogin {

    String message() default "Invalid account login. Pattern: `^[a-zA-Z]{2,}$\n`";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}