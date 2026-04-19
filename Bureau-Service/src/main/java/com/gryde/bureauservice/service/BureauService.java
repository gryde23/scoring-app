package com.gryde.bureauservice.service;

import com.gryde.bureauservice.dto.CreditAccountDto;
import com.gryde.bureauservice.dto.PaymentHistoryDto;
import com.gryde.bureauservice.entity.CreditAccount;
import com.gryde.bureauservice.enums.AccountStatus;
import com.gryde.bureauservice.mapper.CreditAccountMapper;
import com.gryde.bureauservice.mapper.PaymentHistoryMapper;
import com.gryde.bureauservice.projection.CreditAccountsAggProjection;
import com.gryde.bureauservice.projection.PaymentHistoryAggProjection;
import com.gryde.bureauservice.repository.BureauAggregationRepository;
import com.gryde.bureauservice.repository.SelfBanDao;
import com.gryde.contract.BureauSnapshotResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BureauService {

    private final BureauAggregationRepository repository;
    private final BureauScoringEngine scoringEngine;
    private final SelfBanDao selfBanDao;

    public boolean hasSelfBan(UUID userId) {
        return selfBanDao.isBanned(userId);
    }

    public BureauSnapshotResponse collectBureauData(UUID userId) {

        CreditAccountsAggProjection accounts = repository.getAccountsAgg(userId);
        PaymentHistoryAggProjection payments = repository.getPaymentsAgg(userId);

        int score = scoringEngine.calculateBureauScore(accounts, payments);

        return new BureauSnapshotResponse(
                nullToZero(accounts.getTotalAccounts()),
                nullToZero(accounts.getActiveAccounts()),
                nullToZero(accounts.getClosedAccounts()),
                nullToZero(accounts.getDefaultAccounts()),
                nullToZero(accounts.getRestructuredAccounts()),
                nullToZero(accounts.getCreditHistoryDays()),
                nullToZero(accounts.getTotalCreditLimit()),
                nullToZero(accounts.getTotalActiveDebt()),
                nullToZero(accounts.getUtilizationRatio()),
                nullToZero(payments.getTotalPayments()),
                nullToZero(payments.getDpd30()),
                nullToZero(payments.getDpd60()),
                nullToZero(payments.getDpd90()),
                nullToZero(payments.getDpd90Plus()),
                nullToZero(payments.getMaxDaysOverdue()),
                nullToZero(payments.getPaymentRatio()),
                nullToZero(payments.getPartialPaymentsCount()),
                nullToZero(payments.getRecentOverdueCount()),
                nullToZero(accounts.getMonthlyDebtPayment()),
                score
        );

    }

    private Integer nullToZero(Integer value) {
        return value == null ? 0 : value;
    }

    private BigDecimal nullToZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

}
