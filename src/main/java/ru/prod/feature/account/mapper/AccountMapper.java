package ru.prod.feature.account.mapper;

import org.mapstruct.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.prod.entity.Account;
import ru.prod.feature.account.dto.AccountProfilePutRequest;
import ru.prod.feature.account.dto.AccountProfileResponse;
import ru.prod.feature.account.dto.AccountSignUpRequest;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Mapping(target = "avatarUrl", expression = "java(account.getAvatarFileName() != null ? \"api/v1/account/\" + account.getLogin() + \"/avatars\" : null)")
    AccountProfileResponse toProfileResponse(Account account);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", expression = "java(passwordEncoder.encode(request.getPassword()))")
    Account toEntity(AccountSignUpRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "login", ignore = true)
    @Mapping(target = "password", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateAccountFromPutRequest(AccountProfilePutRequest request, @MappingTarget Account account);
}