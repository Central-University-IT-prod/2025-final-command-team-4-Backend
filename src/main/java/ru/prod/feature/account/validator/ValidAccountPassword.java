package ru.prod.feature.account.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AccountPasswordValidator.class)
public @interface ValidAccountPassword {

    String message() default "Invalid account password. Pattern: `^(?=.*[0-9])(?=.*[a-zA-Z\\S]).{8,}$`";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}