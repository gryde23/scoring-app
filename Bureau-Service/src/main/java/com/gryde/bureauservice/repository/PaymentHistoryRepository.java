package com.gryde.bureauservice.repository;

import com.gryde.bureauservice.entity.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, UUID> {

    @Query("""
            select p from PaymentHistory p
            where p.account.id = :accountId
            and p.daysOverdue > 3
            """)
    List<PaymentHistory> findAllOverduePayments(@Param("accountId") UUID accountId);
}
