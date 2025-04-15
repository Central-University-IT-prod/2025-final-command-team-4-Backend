package ru.prod.feature.account.config;

import org.springdoc.core.customizers.PropertyCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.prod.feature.account.validator.ValidAccountAge;
import ru.prod.feature.account.validator.ValidAccountLogin;
import ru.prod.feature.account.validator.ValidAccountPassword;

import java.lang.annotation.Annotation;

@Configuration
public class AccountOpenApiConfig {

    @Bean
    public PropertyCustomizer propertyCustomizer() {
        return (schema, annotatedType) -> {
            if (annotatedType.getCtxAnnotations() != null) {
                for (Annotation annotation : annotatedType.getCtxAnnotations()) {
                    switch (annotation) {
                        case ValidAccountAge age ->
                                schema.setDescription("Дата рождения пользователя (от " + age.minAge() + " до " + age.maxAge() + " лет)");
                        case ValidAccountLogin ignored -> {
                            schema.setDescription("Логин пользователя");
                            schema.setPattern("^@[a-zA-Z]{2,}$");
                        }
                        case ValidAccountPassword ignored -> {
                            schema.setDescription("Пароль пользователя");
                            schema.setPattern("^(?=.*[0-9])(?=.*[a-zA-Z\\S]).{8,}$");
                        }
                        default -> {}
                    }
                }
            }
            return schema;
        };
    }
}
