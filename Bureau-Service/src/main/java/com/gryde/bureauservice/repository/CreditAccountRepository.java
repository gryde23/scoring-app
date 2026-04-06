package com.gryde.bureauservice.repository;

import com.gryde.bureauservice.entity.CreditAccount;
import com.gryde.bureauservice.enums.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CreditAccountRepository extends JpaRepository<CreditAccount, UUID> {

    List<CreditAccount> findAllByPhone(String phone);

    List<CreditAccount> findAllByUserId(UUID userId);
// переделать мб, чтобы возвращать число, а не счета
    List<CreditAccount> findAllByUserIdAndStatus(UUID userId, AccountStatus status);

    @Modifying
    @Query("""
            update CreditAccount
            set userId = :userId
            where phone = :phone
            """
    )
    void setUserId(
            @Param("userId") UUID userId,
            @Param("phone") String phone);
}
