package ru.prod.account;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.multipart.MultipartFile;
import ru.prod.entity.Account;
import ru.prod.feature.account.dto.AccountProfilePutRequest;
import ru.prod.feature.account.dto.AccountProfileResponse;
import ru.prod.feature.account.exception.AccountNotFoundException;
import ru.prod.feature.account.mapper.AccountMapper;
import ru.prod.feature.account.repository.AccountRepository;
import ru.prod.feature.account.service.AccountService;
import ru.prod.common.service.ImageStorageService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {
    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private ImageStorageService imageStorageService;

    @InjectMocks
    private AccountService accountService;

    private Account account;
    private UUID accountId;
    private String login;
    private Account admin;

    @BeforeEach
    void setUp() {
        this.accountId = UUID.randomUUID();
        this.login = "testUser";
        this.account = new Account();
        account.setLogin(login);
        account.setIsAdmin(false);
        account.setIsPrivate(false);

        this.admin = new Account();
        admin.setIsAdmin(true);
        admin.setId(UUID.randomUUID());
    }

    @Test
    void save_ShouldSaveAccount() {
        when(accountRepository.save(account)).thenReturn(account);
        Account result = accountService.save(account);
        assertEquals(account, result);
    }

    @Test
    void existsByLogin_ShouldReturnTrueIfExists() {
        when(accountRepository.existsByLogin(login)).thenReturn(true);
        assertTrue(accountService.existsByLogin(login));

        verify(accountRepository, times(1)).existsByLogin(login);
    }

    @Test
    void existsByLogin_ShouldReturnFalseIfNotExists() {
        when(accountRepository.existsByLogin(login)).thenReturn(false);
        assertFalse(accountService.existsByLogin(login));

        verify(accountRepository, times(1)).existsByLogin(login);
    }

    @Test
    void findByLogin_ShouldReturnAccountIfExists() {
        when(accountRepository.findByLogin(login)).thenReturn(Optional.of(account));
        assertEquals(account, accountService.findByLogin(login));

        verify(accountRepository, times(1)).findByLogin(login);
    }

    @Test
    void findByLogin_ShouldThrowExceptionIfNotExists() {
        when(accountRepository.findByLogin(login)).thenReturn(Optional.empty());
        assertThrows(AccountNotFoundException.class, () -> accountService.findByLogin(login));

        verify(accountRepository, times(1)).findByLogin(login);
    }

    @Test
    void findById_ShouldReturnAccountIfExists() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        assertEquals(account, accountService.findById(accountId));

        verify(accountRepository, times(1)).findById(accountId);
    }

    @Test
    void findById_ShouldThrowExceptionIfNotExists() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());
        assertThrows(AccountNotFoundException.class, () -> accountService.findById(accountId));

        verify(accountRepository, times(1)).findById(accountId);
    }

    @Test
    void put_ShouldUpdateAndReturnProfile() {
        var request = new AccountProfilePutRequest();
        var response = new AccountProfileResponse();

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        doNothing().when(accountMapper).updateAccountFromPutRequest(request, account);
        when(accountRepository.save(account)).thenReturn(account);
        when(accountMapper.toProfileResponse(account)).thenReturn(response);

        var result = accountService.put(accountId, request);
        assertEquals(response, result);

        verify(accountRepository, times(1)).findById(accountId);
        verify(accountMapper, times(1)).updateAccountFromPutRequest(request, account);
        verify(accountRepository, times(1)).save(account);
        verify(accountMapper, times(1)).toProfileResponse(account);
    }

    @Test
    void uploadAvatar_ShouldUploadAndReturnProfile() {
        MultipartFile file = mock(MultipartFile.class);
        String fileName = "avatar.png";
        var response = new AccountProfileResponse();

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(imageStorageService.uploadFile(file)).thenReturn(fileName);
        when(accountRepository.save(account)).thenReturn(account);
        when(accountMapper.toProfileResponse(account)).thenReturn(response);

        var result = accountService.uploadAvatar(accountId, file);
        assertEquals(response, result);

        verify(accountRepository, times(1)).findById(accountId);
        verify(imageStorageService, times(1)).uploadFile(file);
        verify(accountRepository, times(1)).save(account);
        verify(accountMapper, times(1)).toProfileResponse(account);
    }

    @Test
    void downloadAvatar_ShouldReturnAvatarData() {
        String fileName = "avatar.png";
        var resource = mock(InputStreamResource.class);
        account.setAvatarFileName(fileName);

        when(accountRepository.findByLogin(login)).thenReturn(Optional.of(account));
        when(imageStorageService.downloadFile(fileName)).thenReturn(resource);

        var avatarData = accountService.downloadAvatar(login);
        assertEquals(fileName, avatarData.filename());
        assertEquals(resource, avatarData.resource());

        verify(accountRepository, times(1)).findByLogin(login);
        verify(imageStorageService, times(1)).downloadFile(fileName);
    }

    @Test
    void downloadAvatar_ShouldReturnNull_WhenNoAvatarFileName() {
        account.setAvatarFileName(null);

        when(accountRepository.findByLogin(login)).thenReturn(Optional.of(account));

        var result = accountService.downloadAvatar(login);

        assertNull(result);
    }

    @Test
    void getCurrentUserProfile_WhenAccountExists() {
        var profile = new AccountProfileResponse();

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountMapper.toProfileResponse(account)).thenReturn(profile);

        var result = accountService.getCurrentUserProfile(accountId);

        assertNotNull(result);
        assertEquals(profile, result);

        verify(accountRepository, times(1)).findById(accountId);
        verify(accountMapper, times(1)).toProfileResponse(account);
    }

    @Test
    void getCurrentUserProfile_ShouldThrowException_WhenAccountNotFound() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountService.getCurrentUserProfile(accountId));

        verify(accountRepository, times(1)).findById(accountId);
        verifyNoInteractions(accountMapper);
    }

    @Test
    void updateUserProfile_ShouldUpdateProfile_WhenAdmin() {
        var request = new AccountProfilePutRequest();
        var response = new AccountProfileResponse();

        when(accountRepository.findById(admin.getId())).thenReturn(Optional.of(admin));
        when(accountRepository.findByLogin(login)).thenReturn(Optional.of(account));
        doNothing().when(accountMapper).updateAccountFromPutRequest(request, account);
        when(accountRepository.save(account)).thenReturn(account);
        when(accountMapper.toProfileResponse(account)).thenReturn(response);

        var result = accountService.updateUserProfile(admin.getId(), login, request);

        assertNotNull(result);
        assertEquals(response, result);

        verify(accountRepository, times(1)).findById(admin.getId());
        verify(accountRepository, times(1)).findByLogin(login);
        verify(accountMapper, times(1)).updateAccountFromPutRequest(request, account);
        verify(accountRepository, times(1)).save(account);
        verify(accountMapper, times(1)).toProfileResponse(account);
    }

    @Test
    void updateUserProfile_ShouldThrowAccessDeniedException_WhenNotAdmin() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        assertThrows(AccessDeniedException.class,
                () -> accountService.updateUserProfile(accountId, login, new AccountProfilePutRequest()));

        verify(accountRepository, times(1)).findById(accountId);
        verifyNoInteractions(accountMapper);
    }

    @Test
    void updateUserProfile_ShouldThrowAccountNotFoundException_WhenAccountNotFound() {
        when(accountRepository.findById(admin.getId())).thenReturn(Optional.of(admin));
        when(accountRepository.findByLogin(login)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class,
                () -> accountService.updateUserProfile(admin.getId(), login, new AccountProfilePutRequest()));

        verify(accountRepository, times(1)).findById(admin.getId());
        verify(accountRepository, times(1)).findByLogin(login);
        verifyNoInteractions(accountMapper);
    }

    @Test
    void getAccounts_ShouldReturnAllProfiles_WhenAdmin() {
        var pageRequest = PageRequest.of(0, 5);

        List<Account> accounts = List.of(new Account(), new Account());
        Page<Account> accountPage = new PageImpl<>(accounts);
        List<AccountProfileResponse> expectedResponses = List.of(new AccountProfileResponse(), new AccountProfileResponse());

        when(accountRepository.findById(admin.getId())).thenReturn(Optional.of(admin));
        when(accountRepository.findAll(pageRequest)).thenReturn(accountPage);
        when(accountMapper.toProfileResponse(any(Account.class))).thenReturn(new AccountProfileResponse());

        var result = accountService.getAccounts(admin.getId(), 0, 5);

        assertNotNull(result);
        assertEquals(expectedResponses, result);

        verify(accountRepository, times(1)).findById(admin.getId());
        verify(accountRepository, times(1)).findAll(pageRequest);
        verify(accountMapper, times(2)).toProfileResponse(any(Account.class));
    }

    @Test
    void getAccounts_ShouldThrowAccessDeniedException_WhenNotAdmin() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        assertThrows(AccessDeniedException.class,
                () -> accountService.getAccounts(accountId, 0, 5));

        verify(accountRepository, times(1)).findById(accountId);
        verifyNoMoreInteractions(accountRepository, accountMapper);
    }

    @Test
    void getAccounts_ShouldReturnEmptyList_WhenNoAccountsExist() {
        var pageRequest = PageRequest.of(0, 5);

        when(accountRepository.findById(admin.getId())).thenReturn(Optional.of(admin));
        when(accountRepository.findAll(pageRequest)).thenReturn(new PageImpl<>(List.of()));

        var result = accountService.getAccounts(admin.getId(), 0, 5);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(accountRepository, times(1)).findById(admin.getId());
        verify(accountRepository, times(1)).findAll(pageRequest);
        verifyNoMoreInteractions(accountMapper);
    }

    @Test
    void getProfileByLogin_ShouldReturnProfile_WhenPublicProfile() {
        var response = new AccountProfileResponse();

        when(accountRepository.findByLogin(login)).thenReturn(Optional.of(account));
        when(accountMapper.toProfileResponse(account)).thenReturn(response);

        var result = accountService.getProfileByLogin(login);

        assertNotNull(result);
        assertEquals(response, result);

        verify(accountRepository, times(1)).findByLogin(login);
        verify(accountMapper, times(1)).toProfileResponse(account);
    }

    @Test
    void getProfileByLogin_ShouldThrowAccessDeniedException_WhenPrivateProfile() {
        account.setIsPrivate(true);
        when(accountRepository.findByLogin(login)).thenReturn(Optional.of(account));

        assertThrows(AccessDeniedException.class,
                () -> accountService.getProfileByLogin(login));

        verify(accountRepository, times(1)).findByLogin(login);
        verifyNoMoreInteractions(accountMapper);
    }

    @Test
    void getProfileByLogin_ShouldThrowAccountNotFoundException_WhenAccountDoesNotExist() {
        when(accountRepository.findByLogin(login)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class,
                () -> accountService.getProfileByLogin(login));

        verify(accountRepository, times(1)).findByLogin(login);
        verifyNoMoreInteractions(accountMapper);
    }
}
