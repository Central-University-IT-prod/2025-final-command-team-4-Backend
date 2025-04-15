package ru.prod.feature.account.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.prod.feature.account.api.AccountSignUpApi;
import ru.prod.feature.account.dto.AccountSignUpRequest;
import ru.prod.feature.account.dto.AccountSignUpResponse;
import ru.prod.feature.account.service.AccountAuthService;

@RestController
@RequiredArgsConstructor
public class AccountSignUpController implements AccountSignUpApi {

    private final AccountAuthService accountAuthService;

    @Override
    public AccountSignUpResponse signUp(AccountSignUpRequest request) {
        return accountAuthService.signUp(request);
    }
}
