package ru.prod.feature.account.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.prod.feature.account.api.AccountProfileApi;
import ru.prod.feature.account.dto.AccountProfilePutRequest;
import ru.prod.feature.account.dto.AccountProfileResponse;
import ru.prod.feature.account.service.AccountService;
import ru.prod.feature.account.help.ImageData;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AccountProfileController implements AccountProfileApi {

    private final AccountService accountService;

    @Override
    public AccountProfileResponse getProfile(String login) {
        return accountService.getProfileByLogin(login);
    }

    @Override
    public AccountProfileResponse putProfile(UUID accountId, AccountProfilePutRequest request) {
        return accountService.put(accountId, request);
    }

    @Override
    public AccountProfileResponse getCurrentUserProfile(UUID accountId) {
        return accountService.getCurrentUserProfile(accountId);
    }

    @Override
    public ResponseEntity<AccountProfileResponse> uploadAvatar(UUID accountId, MultipartFile file) {
        AccountProfileResponse profile = accountService.uploadAvatar(accountId, file);
        return ResponseEntity.ok(profile);
    }

    @Override
    public ResponseEntity<InputStreamResource> downloadAvatar(String login) {
        ImageData imageData = accountService.downloadAvatar(login);

        if (imageData == null) {
            return ResponseEntity.noContent().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + imageData.filename());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .headers(headers)
                .body(imageData.resource());
    }

    @Override
    public AccountProfileResponse updateUserProfile(UUID accountId, String login, AccountProfilePutRequest putRequest) {
        return accountService.updateUserProfile(accountId, login, putRequest);
    }

    @Override
    public List<AccountProfileResponse> getAccounts(UUID accountId, Integer page, Integer size) {
        return accountService.getAccounts(accountId, page, size);
    }

}
