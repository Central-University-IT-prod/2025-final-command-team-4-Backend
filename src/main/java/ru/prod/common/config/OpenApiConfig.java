package ru.prod.common.config;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.media.Schema;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@OpenAPIDefinition(
        info = @Info(
                title = "CRM API",
                version = "1.0.0"
        )
)
@SecurityScheme(
        name = "JWT",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
@Configuration
public class OpenApiConfig {

    @Bean
    @SuppressWarnings({"rawtypes", "unchecked"})
    public OpenApiCustomizer propertyNamingStrategyCustomizer(ObjectMapper objectMapper) {
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

        return openApi -> {
            if (openApi.getComponents() != null) {
                Map<String, Schema> schemas = openApi.getComponents().getSchemas();
                if (schemas != null) {
                    schemas.forEach((name, schema) -> {
                        Map<String, Schema<?>> properties = schema.getProperties();
                        if (properties != null) {
                            Map<String, Schema<?>> newProperties = new LinkedHashMap<>();
                            for (Map.Entry<String, Schema<?>> entry : properties.entrySet()) {
                                String originalKey = entry.getKey();
                                Schema<?> value = entry.getValue();
                                String snakeCaseKey = toSnakeCase(originalKey);
                                newProperties.put(snakeCaseKey, value);
                            }
                            schema.setProperties(newProperties);
                        }

                        if (schema.getRequired() != null)
                            schema.setRequired(schema.getRequired().stream().map(this::toSnakeCase).toList());
                    });
                }
            }
        };
    }

    private String toSnakeCase(Object original) {
        return original.toString().replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }
}
