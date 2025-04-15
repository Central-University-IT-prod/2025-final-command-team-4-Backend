package ru.prod.feature.coworking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.prod.entity.Account;

import java.util.UUID;

@Repository
public interface CoworkingAccountRepository extends JpaRepository<Account, UUID> {

    @Query("""
    SELECT account
    FROM Account account
    WHERE account.id = :accountId
    """)
    Account getByAccountId(UUID accountId);
}
