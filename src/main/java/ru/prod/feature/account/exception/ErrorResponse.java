package ru.prod.feature.account.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ErrorResponse {

    private Integer status;
    private String error;
}
