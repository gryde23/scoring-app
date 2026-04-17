package com.gryde.bureauservice.entity;

import com.gryde.bureauservice.enums.AccountStatus;
import com.gryde.bureauservice.enums.AccountType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "credit_accounts")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreditAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "account_type", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private AccountType accountType;

    @Column(name = "open_date", nullable = false)
    private LocalDate openDate;

    @Column(name = "close_date")
    private LocalDate closeDate;

    @Column(name = "original_amount", nullable = false)
    private BigDecimal originalAmount;

    @Column(name = "current_balance", nullable = false)
    private BigDecimal currentBalance;

    @Column(name = "monthly_payment", nullable = false)
    private BigDecimal monthlyPayment;

    @Column(name = "status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private AccountStatus status;

    @Column(name = "bank_name")
    private String bankName;

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<PaymentHistory> payments;
}
