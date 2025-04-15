package ru.prod.feature.account.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.prod.feature.account.validator.ValidAccountAge;

import java.time.LocalDate;

@Data
public class AccountProfilePutRequest {

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    @ValidAccountAge
    private LocalDate birthDay;

    @NotNull
    private Boolean isPrivate;
}
