package com.gryde.applicationorchestrator.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "application_bureau_snapshot")
@Data
public class BureauSnapshot {

    @Id
    @Column(name = "application_id", nullable = false)
    private UUID applicationId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "application_id")
    private Application application;

    @Column(name = "total_accounts", nullable = false)
    private Integer totalAccounts;

    @Column(name = "active_accounts", nullable = false)
    private Integer activeAccounts;

    @Column(name = "closed_accounts", nullable = false)
    private Integer closedAccounts;

    @Column(name = "default_accounts", nullable = false)
    private Integer defaultAccounts;

    @Column(name = "restructured_accounts", nullable = false)
    private Integer restructuredAccounts;

    @Column(name = "credit_history_days", nullable = false)
    private Integer creditHistoryDays;

    @Column(name = "total_credit_limit", nullable = false, precision = 14, scale = 2)
    private BigDecimal totalCreditLimit;

    @Column(name = "total_active_debt", nullable = false, precision = 14, scale = 2)
    private BigDecimal totalActiveDebt;

    @Column(name = "utilization_ratio", nullable = false, precision = 8, scale = 4)
    private BigDecimal utilizationRatio;

    @Column(name = "total_payments", nullable = false)
    private Integer totalPayments;

    @Column(name = "dpd30", nullable = false)
    private Integer dpd30;

    @Column(name = "dpd60", nullable = false)
    private Integer dpd60;

    @Column(name = "dpd90", nullable = false)
    private Integer dpd90;

    @Column(name = "dpd90_plus", nullable = false)
    private Integer dpd90Plus;

    @Column(name = "max_days_overdue", nullable = false)
    private Integer maxDaysOverdue;

    @Column(name = "payment_ratio", nullable = false, precision = 8, scale = 4)
    private BigDecimal paymentRatio;

    @Column(name = "partial_payments_count", nullable = false)
    private Integer partialPaymentsCount;

    @Column(name = "recent_overdue_count", nullable = false)
    private Integer recentOverdueCount;

    @Column(name = "monthly_debt_payment", nullable = false, precision = 14, scale = 2)
    private BigDecimal monthlyDebtPayment;

    @Column(name = "debt_to_income", nullable = false, precision = 8, scale = 4)
    private BigDecimal debtToIncome;

    @Column(name = "bureau_score")
    private Integer bureauScore;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
