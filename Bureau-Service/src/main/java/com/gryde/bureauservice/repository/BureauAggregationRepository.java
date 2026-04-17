package com.gryde.bureauservice.repository;

import com.gryde.bureauservice.entity.CreditAccount;
import com.gryde.bureauservice.projection.CreditAccountsAggProjection;
import com.gryde.bureauservice.projection.PaymentHistoryAggProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface BureauAggregationRepository extends Repository<CreditAccount, UUID> {

    @Query(value = """
        SELECT
            COUNT(*) AS totalAccounts,
            COUNT(*) FILTER (WHERE ca.status = 'ACTIVE') AS activeAccounts,
            COUNT(*) FILTER (WHERE ca.status = 'CLOSED') AS closedAccounts,
            COUNT(*) FILTER (WHERE ca.status = 'DEFAULT') AS defaultAccounts,
            COUNT(*) FILTER (WHERE ca.status = 'RESTRUCTURED') AS restructuredAccounts,

            CAST(CURRENT_DATE - MIN(ca.open_date) AS integer) AS creditHistoryDays,

            COALESCE(SUM(ca.original_amount) FILTER (WHERE ca.status = 'ACTIVE'), 0) AS totalCreditLimit,
            COALESCE(SUM(ca.current_balance) FILTER (WHERE ca.status = 'ACTIVE'), 0) AS totalActiveDebt,
            COALESCE(SUM(ca.monthly_payment) FILTER (WHERE ca.status = 'ACTIVE'), 0) AS monthlyDebtPayment,

            ROUND(
                    COALESCE(
                        SUM(ca.current_balance) FILTER (
                            WHERE ca.account_type = 'CREDIT_CARD'
                              AND ca.status = 'ACTIVE'
                        )
                        /
                        NULLIF(
                            SUM(ca.original_amount) FILTER (
                                WHERE ca.account_type = 'CREDIT_CARD'
                                  AND ca.status = 'ACTIVE'
                            ),
                            0
                        ),
                        0
                    ),
                    4
                ) AS utilization_ratio

        FROM credit_accounts ca
        WHERE ca.user_id = :userId
        """, nativeQuery = true)
    CreditAccountsAggProjection getAccountsAgg(@Param("userId") UUID userId);


    @Query(value = """
        SELECT
            COUNT(ph.id) AS totalPayments,

            COUNT(ph.id) FILTER (WHERE ph.days_overdue BETWEEN 1 AND 30) AS dpd30,
            COUNT(ph.id) FILTER (WHERE ph.days_overdue BETWEEN 31 AND 60) AS dpd60,
            COUNT(ph.id) FILTER (WHERE ph.days_overdue BETWEEN 61 AND 90) AS dpd90,
            COUNT(ph.id) FILTER (WHERE ph.days_overdue > 90) AS dpd90Plus,

            COALESCE(MAX(ph.days_overdue), 0) AS maxDaysOverdue,

            ROUND(
                COALESCE(
                    SUM(ph.amount_paid) / NULLIF(SUM(ph.amount_due), 0),
                    1
                ),
                4
            ) AS paymentRatio,

            COUNT(ph.id) FILTER (
                WHERE ph.status = 'PARTIAL'
                   OR ph.amount_paid < ph.amount_due
            ) AS partialPaymentsCount,

            COUNT(ph.id) FILTER (
                WHERE ph.days_overdue > 0
                  AND ph.due_date >= CURRENT_DATE - INTERVAL '6 months'
            ) AS recentOverdueCount

        FROM credit_accounts ca
        LEFT JOIN payment_history ph ON ph.account_id = ca.id
        WHERE ca.user_id = :userId
        """, nativeQuery = true)
    PaymentHistoryAggProjection getPaymentsAgg(@Param("userId") UUID userId);
}
