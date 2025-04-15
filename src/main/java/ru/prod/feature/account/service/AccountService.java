package ru.prod.feature.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.prod.common.service.ImageStorageService;
import ru.prod.entity.Account;
import ru.prod.feature.account.dto.AccountProfilePutRequest;
import ru.prod.feature.account.dto.AccountProfileResponse;
import ru.prod.feature.account.exception.AccountNotFoundException;
import ru.prod.feature.account.help.ImageData;
import ru.prod.feature.account.mapper.AccountMapper;
import ru.prod.feature.account.repository.AccountRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final ImageStorageService imageStorageService;

    public Account save(Account account) {
        return accountRepository.save(account);
    }

    public boolean existsByLogin(String login) {
        return accountRepository.existsByLogin(login);
    }

    public Account findByLogin(String login) {
        return accountRepository.findByLogin(login)
                .orElseThrow(() -> new AccountNotFoundException("Account with login " + login + " not found"));
    }

    public AccountProfileResponse getProfileByLogin(String login) {
        Account account = findByLogin(login);

        if (account.getIsPrivate() && !account.getIsAdmin()) {
            throw new AccessDeniedException("This user's profile is private");
        }

        return accountMapper.toProfileResponse(account);
    }

    public Account findById(UUID accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account with id " + accountId + " not found"));
    }

    public AccountProfileResponse getCurrentUserProfile(UUID accountId) {
        Account account = findById(accountId);
        return accountMapper.toProfileResponse(account);
    }

    public AccountProfileResponse put(UUID accountId, AccountProfilePutRequest request) {
        Account account = findById(accountId);
        accountMapper.updateAccountFromPutRequest(request, account);

        return accountMapper.toProfileResponse(save(account));
    }

    public AccountProfileResponse uploadAvatar(UUID accountId, MultipartFile file) {
        Account account = findById(accountId);

        String imageName = imageStorageService.uploadFile(file);
        account.setAvatarFileName(imageName);
        account = save(account);

        return accountMapper.toProfileResponse(account);
    }

    public ImageData downloadAvatar(String login) {
        Account account = findByLogin(login);

        if (account.getAvatarFileName() == null) {
            return null;
        }

        InputStreamResource resource = imageStorageService.downloadFile(account.getAvatarFileName());

        return new ImageData(resource, account.getAvatarFileName());
    }

    public AccountProfileResponse updateUserProfile(UUID accountId, String login, AccountProfilePutRequest putRequest) {
        Account admin = findById(accountId);

        if (!admin.getIsAdmin()) {
            throw new AccessDeniedException("You don't have permission to update user profile");
        }

        Account account = findByLogin(login);
        accountMapper.updateAccountFromPutRequest(putRequest, account);
        account = save(account);

        return accountMapper.toProfileResponse(account);
    }

    public List<AccountProfileResponse> getAccounts(UUID accountId, int page, int size) {
        isAdmin(accountId);

        return accountRepository.findAll(PageRequest.of(page, size))
                .stream()
                .map(accountMapper::toProfileResponse)
                .toList();
    }

    private void isAdmin(UUID accountId) {
        Account account = findById(accountId);

        if (!account.getIsAdmin()) {
            throw new AccessDeniedException("You don't have permission to get accounts");
        }
    }
}
