package ru.prod.feature.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.prod.common.security.jwt.JwtProvider;
import ru.prod.entity.Account;
import ru.prod.feature.account.dto.*;
import ru.prod.feature.account.exception.AccountAlreadyExistsException;
import ru.prod.feature.account.exception.BadCredentialsException;
import ru.prod.feature.account.mapper.AccountMapper;

@Service
@RequiredArgsConstructor
public class AccountAuthService {

    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final AccountService accountService;
    private final AccountMapper accountMapper;

    public AccountSignUpResponse signUp(AccountSignUpRequest request) {
        if (accountService.existsByLogin("@" + request.getLogin())) {
            throw new AccountAlreadyExistsException("Account with login " + request.getLogin() + " already exists.");
        }

        Account account = accountMapper.toEntity(request);
        account.setIsPrivate(false);
        account.setLogin("@" + account.getLogin());
        account = accountService.save(account);

        String accessToken = jwtProvider.generateToken(account.getId(), false);
        String refreshToken = jwtProvider.generateToken(account.getId(), true);

        AccountProfileResponse profile = accountMapper.toProfileResponse(account);

        return new AccountSignUpResponse(accessToken, refreshToken, profile);
    }

    public AccountSignInResponse signIn(AccountSignInRequest request) {
        Account account = accountService.findByLogin("@" + request.getLogin());

        if (!passwordEncoder.matches(request.getPassword(), account.getPassword())) {
            throw new BadCredentialsException("Wrong login or password");
        }

        String accessToken = jwtProvider.generateToken(account.getId(), false);
        String refreshToken = jwtProvider.generateToken(account.getId(), true);

        AccountProfileResponse profile = accountMapper.toProfileResponse(account);

        return new AccountSignInResponse(accessToken, refreshToken, profile);
    }
}
