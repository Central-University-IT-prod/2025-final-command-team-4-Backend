package ru.prod.feature.account.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountSignUpResponse {

    @NotNull
    private String accessToken;

    @NotNull
    private String refreshToken;

    @NotNull
    private AccountProfileResponse profile;
}
