package ru.prod.feature.account.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.prod.feature.account.validator.ValidAccountAge;
import ru.prod.feature.account.validator.ValidAccountLogin;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class AccountProfileResponse {

    @NotNull
    @ValidAccountLogin
    private String login;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @ValidAccountAge
    private LocalDate birthDay;

    private String avatarUrl;

    private Boolean isPrivate;

    private Boolean isAdmin;

    public AccountProfileResponse(String login, String firstName, String lastName, LocalDate birthDay, String avatarFileName, Boolean isPrivate, Boolean isAdmin) {
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDay = birthDay;
        this.avatarUrl = avatarFileName != null ? "api/v1/account/" + login + "/avatars" : null;
        this.isPrivate = isPrivate;
        this.isAdmin = isAdmin;
    }
}
