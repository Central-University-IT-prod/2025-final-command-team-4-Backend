package ru.prod.feature.account.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.prod.feature.account.validator.ValidAccountAge;
import ru.prod.feature.account.validator.ValidAccountLogin;
import ru.prod.feature.account.validator.ValidAccountPassword;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AccountSignUpRequest {

    @NotNull
    @ValidAccountLogin
    private String login;

    @NotNull
    @ValidAccountPassword
    private String password;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @ValidAccountAge
    private LocalDate birthDay;

    @NotNull
    private Boolean isAdmin;
}
