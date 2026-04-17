package com.gryde.bureauservice.service;

import com.gryde.bureauservice.projection.CreditAccountsAggProjection;
import com.gryde.bureauservice.projection.PaymentHistoryAggProjection;
import com.gryde.bureauservice.repository.BureauAggregationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
public class BureauScoringEngine {

    public int calculateBureauScore(CreditAccountsAggProjection accounts, PaymentHistoryAggProjection payments) {
        if (accounts.getTotalAccounts() == null || accounts.getTotalAccounts() == 0) {
            return 600;
        }

        int score = 500;

        score += paymentBonus(payments);
        score += utilizationBonus(accounts);
        score += historyBonus(accounts);
        score += penalty(accounts);

        return Math.max(0, Math.min(score, 1000));
    }

    private int paymentBonus(PaymentHistoryAggProjection payments) {
        if (payments.getTotalPayments() == 0) return 0;

        int points = 0;

        double ratio = payments.getPaymentRatio().doubleValue();
        if (ratio >= 0.95) points += 150;
        else if (ratio >= 0.80) points += 80;
        else if (ratio >= 0.60) points += 20;
        else points -= 80;

        points -= payments.getDpd30() * 20;
        points -= payments.getDpd60() * 40;
        points -= payments.getDpd90() * 60;
        points -= payments.getDpd90Plus() * 80;

        points -= payments.getRecentOverdueCount() * 30;

        points -= payments.getPartialPaymentsCount() * 10;

        return points;
    }

    private int utilizationBonus(CreditAccountsAggProjection accounts) {
        if (accounts.getActiveAccounts() == 0) return 0;

        double ratio = accounts.getUtilizationRatio().doubleValue();

        if (ratio <= 0.10) return  200;
        else if (ratio <= 0.30) return 120;
        else if (ratio <= 0.50) return 40;
        else if (ratio <= 0.75) return -20;
        else return -100;
    }

    private int historyBonus(CreditAccountsAggProjection accounts) {
        int days = accounts.getCreditHistoryDays();

        int points = 0;

        if (days >= 365 * 7) points += 100;
        else if (days >= 365 * 5) points += 80;
        else if (days >= 365 * 3) points += 60;
        else if (days >= 365) points += 35;
        else if (days >= 180) points += 15;

        points += accounts.getClosedAccounts() * 20;

        return points;
    }

    private int penalty(CreditAccountsAggProjection accounts) {
        int points = 0;
        points -= accounts.getDefaultAccounts() * 200;
        points -= accounts.getRestructuredAccounts() * 100;
        return points;
    }
}
