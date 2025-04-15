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
import ru.prod.feature.account.dto.AccountSignUpRequest;
import ru.prod.feature.account.dto.AccountSignUpResponse;

@Tag(name = "Account")
@RequestMapping("/api/v1/account")
public interface AccountSignUpApi {

    @Operation(summary = "sign-up")
    @ApiResponses({
        @ApiResponse(responseCode = "201", useReturnTypeSchema = true),
        @ApiResponse(responseCode = "400"),
        @ApiResponse(responseCode = "401"),
        @ApiResponse(responseCode = "409")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/sign-up")
    AccountSignUpResponse signUp(@RequestBody @Valid AccountSignUpRequest request);
}
