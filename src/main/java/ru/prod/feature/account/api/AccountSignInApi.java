package ru.prod.feature.account.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.prod.feature.account.dto.AccountSignInRequest;
import ru.prod.feature.account.dto.AccountSignInResponse;

@Tag(name = "Account")
@RequestMapping("/api/v1/account")
public interface AccountSignInApi {

    @Operation(summary = "sign-in")
    @ApiResponses({
        @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
        @ApiResponse(responseCode = "400"),
        @ApiResponse(responseCode = "401")
    })
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/sign-in")
    AccountSignInResponse signIn(@RequestBody @Valid AccountSignInRequest request);
}
