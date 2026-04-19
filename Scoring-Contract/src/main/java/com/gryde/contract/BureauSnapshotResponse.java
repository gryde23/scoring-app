package com.gryde.contract;

import java.math.BigDecimal;

public record BureauSnapshotResponse(
        Integer totalAccounts,
        Integer activeAccounts,
        Integer closedAccounts,
        Integer defaultAccounts,
        Integer restructuredAccounts,

        Integer creditHistoryDays,
        BigDecimal totalCreditLimit,
        BigDecimal totalActiveDebt,
        BigDecimal utilizationRatio,

        Integer totalPayments,
        Integer dpd30,
        Integer dpd60,
        Integer dpd90,
        Integer dpd90Plus,
        Integer maxDaysOverdue,

        BigDecimal paymentRatio,
        Integer partialPaymentsCount,
        Integer recentOverdueCount,

        BigDecimal monthlyDebtPayment,

        Integer bureauScore
) {
}
