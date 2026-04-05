package com.gryde.bureauservice.repository;

import com.gryde.bureauservice.entity.CreditAccount;
import com.gryde.bureauservice.enums.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CreditAccountRepository extends JpaRepository<CreditAccount, UUID> {

    List<CreditAccount> findAllByPhone(String phone);

    List<CreditAccount> findAllByUserId(UUID userId);

    List<CreditAccount> findAllByUserIdAndStatus(UUID userId, AccountStatus status);
}
