package ru.prod.feature.account.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.prod.feature.account.dto.AccountProfilePutRequest;
import ru.prod.feature.account.dto.AccountProfileResponse;
import ru.prod.feature.account.validator.ValidAccountLogin;

import java.util.List;
import java.util.UUID;

@Tag(name = "Account")
@RequestMapping("/api/v1/account")
public interface AccountProfileApi {

    @Operation(summary = "get-profile")
    @ApiResponses({
        @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
        @ApiResponse(responseCode = "400"),
        @ApiResponse(responseCode = "404")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{login}/profile")
    AccountProfileResponse getProfile(@PathVariable String login);

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "put-profile")
    @ApiResponses({
        @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
        @ApiResponse(responseCode = "400"),
        @ApiResponse(responseCode = "401")
    })
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/@me/profile")
    AccountProfileResponse putProfile(
            @RequestAttribute("accountId") UUID accountId,
            @RequestBody @Valid AccountProfilePutRequest request);

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "get-current-user-profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/@me/profile")
    AccountProfileResponse getCurrentUserProfile(
            @RequestAttribute("accountId") UUID accountId
    );

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "upload-avatar")
    @ApiResponses({
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404")
    })
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/avatars")
    ResponseEntity<AccountProfileResponse> uploadAvatar(
            @RequestAttribute("accountId") UUID accountId,
            @RequestParam("file") MultipartFile file
    );

    @Operation(summary = "download-avatar")
    @ApiResponses({
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("{login}/avatars")
    ResponseEntity<InputStreamResource> downloadAvatar(@PathVariable("login") String login);

    @Operation(summary = "update-account-profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "403"),
            @ApiResponse(responseCode = "404")
    })
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{login}/profile")
    AccountProfileResponse updateUserProfile(
            @RequestAttribute("accountId") UUID accountId,
            @PathVariable("login") String login,
            @RequestBody @Valid AccountProfilePutRequest putRequest
    );

    @Operation(summary = "get-accounts")
    @ApiResponses({
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "403")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    List<AccountProfileResponse> getAccounts(
            @RequestAttribute("accountId") UUID accountId,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size
    );
}
