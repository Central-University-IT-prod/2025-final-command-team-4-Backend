package ru.prod.feature.account.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.prod.feature.account.validator.ValidAccountLogin;
import ru.prod.feature.account.validator.ValidAccountPassword;

@Data
public class AccountSignInRequest {

    @NotNull
    @ValidAccountLogin
    private String login;

    @NotNull
    @ValidAccountPassword
    private String password;
}
