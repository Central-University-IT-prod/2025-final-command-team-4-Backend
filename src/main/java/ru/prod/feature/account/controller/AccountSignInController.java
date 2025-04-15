package ru.prod.feature.account.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.prod.common.exception.TodoException;
import ru.prod.feature.account.api.AccountSignInApi;
import ru.prod.feature.account.dto.AccountProfileResponse;
import ru.prod.feature.account.dto.AccountSignInRequest;
import ru.prod.feature.account.dto.AccountSignInResponse;
import ru.prod.feature.account.service.AccountAuthService;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AccountSignInController implements AccountSignInApi {

    private final AccountAuthService accountAuthService;

    @Override
    public AccountSignInResponse signIn(AccountSignInRequest request) {
        return accountAuthService.signIn(request);
    }
}
