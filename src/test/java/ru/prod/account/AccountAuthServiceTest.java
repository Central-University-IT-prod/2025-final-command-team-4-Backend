package ru.prod.account;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.prod.common.security.jwt.JwtProvider;
import ru.prod.entity.Account;
import ru.prod.feature.account.dto.AccountProfileResponse;
import ru.prod.feature.account.dto.AccountSignInRequest;
import ru.prod.feature.account.dto.AccountSignUpRequest;
import ru.prod.feature.account.exception.AccountAlreadyExistsException;
import ru.prod.feature.account.exception.BadCredentialsException;
import ru.prod.feature.account.mapper.AccountMapper;
import ru.prod.feature.account.service.AccountAuthService;
import ru.prod.feature.account.service.AccountService;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountAuthServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private AccountService accountService;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountAuthService accountAuthService;

    private AccountSignUpRequest signUpRequest;
    private AccountSignInRequest signInRequest;
    private Account account;
    private AccountProfileResponse profileResponse;

    @BeforeEach
    void setUp() {
        signUpRequest = new AccountSignUpRequest();
        signUpRequest.setLogin("testLogin");
        signUpRequest.setPassword("testPassword");
        signUpRequest.setFirstName("John");
        signUpRequest.setLastName("Doe");
        signUpRequest.setBirthDay(LocalDate.of(1990, 1, 1));

        signInRequest = new AccountSignInRequest();
        signInRequest.setLogin("testLogin");
        signInRequest.setPassword("testPassword");

        account = new Account();
        account.setId(UUID.randomUUID());
        account.setLogin("testLogin");
        account.setPassword("encodedPassword");
        account.setFirstName("John");
        account.setLastName("Doe");
        account.setBirthDay(LocalDate.of(1990, 1, 1));

        profileResponse = new AccountProfileResponse();
        profileResponse.setLogin("testLogin");
        profileResponse.setFirstName("John");
        profileResponse.setLastName("Doe");
        profileResponse.setBirthDay(LocalDate.of(1990, 1, 1));
        profileResponse.setAvatarUrl("https://example.com/avatar.jpg");
    }

    @Test
    void signUp_ShouldCreateAccount_WhenAccountDoesNotExist() {
        when(accountService.existsByLogin("@" + signInRequest.getLogin())).thenReturn(false);
        when(accountMapper.toEntity(signUpRequest)).thenReturn(account);
        when(accountService.save(account)).thenReturn(account);
        when(jwtProvider.generateToken(account.getId(), false)).thenReturn("accessToken");
        when(jwtProvider.generateToken(account.getId(), true)).thenReturn("refreshToken");
        when(accountMapper.toProfileResponse(account)).thenReturn(profileResponse);

        var response = accountAuthService.signUp(signUpRequest);
        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());

        verify(accountService, times(1)).existsByLogin("@" + signUpRequest.getLogin());
        verify(accountMapper, times(1)).toEntity(signUpRequest);
        verify(accountService, times(1)).save(account);
        verify(jwtProvider, times(1)).generateToken(account.getId(), false);
        verify(jwtProvider, times(1)).generateToken(account.getId(), true);
        verify(accountMapper, times(1)).toProfileResponse(account);
    }

    @Test
    void signUp_ShouldThrowException_WhenAccountExists() {
        when(accountService.existsByLogin("@" + signUpRequest.getLogin())).thenReturn(true);

        assertThrows(AccountAlreadyExistsException.class, () -> accountAuthService.signUp(signUpRequest));
        verify(accountService, times(1)).existsByLogin("@" + signUpRequest.getLogin());
        verifyNoMoreInteractions(accountMapper, accountService, jwtProvider);
    }

    @Test
    void signIn_ShouldReturnSignInResponse_WhenCredentialsAreValid() {
        when(accountService.findByLogin("@" + signInRequest.getLogin())).thenReturn(account);
        when(passwordEncoder.matches(signInRequest.getPassword(), account.getPassword())).thenReturn(true);
        when(jwtProvider.generateToken(account.getId(), false)).thenReturn("accessToken");
        when(jwtProvider.generateToken(account.getId(), true)).thenReturn("refreshToken");
        when(accountMapper.toProfileResponse(account)).thenReturn(profileResponse);

        var response = accountAuthService.signIn(signInRequest);

        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
        assertEquals(profileResponse, response.getProfile());

        verify(accountService, times(1)).findByLogin("@" + signInRequest.getLogin());
        verify(passwordEncoder, times(1)).matches(signInRequest.getPassword(), account.getPassword());
        verify(jwtProvider, times(1)).generateToken(account.getId(), false);
        verify(jwtProvider, times(1)).generateToken(account.getId(), true);
        verify(accountMapper, times(1)).toProfileResponse(account);
    }

    @Test
    void signIn_ShouldThrowException_WhenPasswordIsInvalid() {
        when(accountService.findByLogin("@" + signInRequest.getLogin())).thenReturn(account);
        when(passwordEncoder.matches(signInRequest.getPassword(), account.getPassword()))
                .thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> accountAuthService.signIn(signInRequest));
    }
}
