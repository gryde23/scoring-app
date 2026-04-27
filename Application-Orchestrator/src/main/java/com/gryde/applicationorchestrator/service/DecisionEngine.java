package com.gryde.applicationorchestrator.service;

import com.gryde.applicationorchestrator.dto.DecisionResult;
import com.gryde.contract.AntifraudResponse;
import com.gryde.contract.ScoringResponse;
import com.gryde.contract.enums.FinalDecision;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class DecisionEngine {
    private static final BigDecimal REJECT_DEFAULT_PROBABILITY = new BigDecimal("0.10");
    private static final BigDecimal HIGH_DEFAULT_PROBABILITY = new BigDecimal("0.08");
    private static final BigDecimal ELEVATED_DEFAULT_PROBABILITY = new BigDecimal("0.07");
    private static final BigDecimal MODERATE_DEFAULT_PROBABILITY = new BigDecimal("0.05");

    private static final int HIGH_ANTIFRAUD_SCORE = 500;
    private static final int MEDIUM_ANTIFRAUD_SCORE = 250;
    private static final int ELEVATED_ANTIFRAUD_SCORE = 150;

    private static final int MIN_BUREAU_SCORE = 600;
    private static final int BORDERLINE_BUREAU_SCORE = 650;
    private static final int MODERATE_BUREAU_SCORE = 700;

    private static final int LOW_INTERNAL_SCORE = 450;
    private static final int BORDERLINE_INTERNAL_SCORE = 500;
    private static final int MODERATE_INTERNAL_SCORE = 650;

    private static final int MIN_APPROVED_LIMIT = 10_000;
    private static final int LIMIT_ROUNDING_STEP = 10_000;

    public DecisionResult decide(ScoringResponse scoring, Integer bureauScore, AntifraudResponse fraud) {
        List<String> decisionReasons = new ArrayList<>();

        collectRejectReasons(scoring, bureauScore, fraud, decisionReasons);
        if (!decisionReasons.isEmpty()) {
            decisionReasons.addAll(scoring.scoringReasons());
            return new DecisionResult(FinalDecision.REJECTED, null, decisionReasons);
        }

        collectManualReviewReasons(scoring, bureauScore, fraud, decisionReasons);
        if (!decisionReasons.isEmpty()) {
            decisionReasons.addAll(scoring.scoringReasons());
            return new DecisionResult(FinalDecision.MANUAL_REVIEW, null, decisionReasons);
        }

        int approvedLimit = calculateApprovedLimit(scoring, bureauScore, fraud);
        if (approvedLimit < MIN_APPROVED_LIMIT) {
            decisionReasons.add("Расчетный лимит ниже минимального порога");
            decisionReasons.addAll(scoring.scoringReasons());
            return new DecisionResult(FinalDecision.MANUAL_REVIEW, null, decisionReasons);
        }

        return new DecisionResult(FinalDecision.APPROVED, approvedLimit, List.of());
    }

    private void collectRejectReasons(
            ScoringResponse scoring,
            Integer bureauScore,
            AntifraudResponse fraud,
            List<String> decisionReasons
    ) {
        BigDecimal defaultProbability = scoring.mlDefaultProbability();
        int antifraudScore = fraud.antifraudScore();
        int internalScore = scoring.internalScore();
        Integer recommendedLimit = scoring.recommendedLimit();

        if (internalScore <= 0) {
            decisionReasons.add("Внутренний скоринг выявил стоп-фактор");
        }

        if (isGreaterThan(defaultProbability, REJECT_DEFAULT_PROBABILITY)) {
            decisionReasons.add("Риск дефолта выше 10%");
        }

        if (antifraudScore > HIGH_ANTIFRAUD_SCORE) {
            decisionReasons.add("Высокий антифрод риск");
        }

        if (bureauScore < MIN_BUREAU_SCORE) {
            decisionReasons.add("Плохая кредитная история");
        }

        if (recommendedLimit == null || recommendedLimit <= 0) {
            decisionReasons.add("Рекомендованный лимит отсутствует");
        }

        if (isGreaterOrEqual(defaultProbability, HIGH_DEFAULT_PROBABILITY)
                && bureauScore < BORDERLINE_BUREAU_SCORE) {
            decisionReasons.add("Высокий риск дефолта при слабой кредитной истории");
        }

        if (isGreaterOrEqual(defaultProbability, ELEVATED_DEFAULT_PROBABILITY)
                && antifraudScore >= MEDIUM_ANTIFRAUD_SCORE) {
            decisionReasons.add("Повышенный риск дефолта при среднем антифрод риске");
        }

        if (bureauScore < BORDERLINE_BUREAU_SCORE && antifraudScore >= MEDIUM_ANTIFRAUD_SCORE) {
            decisionReasons.add("Слабая кредитная история при среднем антифрод риске");
        }

        if (internalScore < LOW_INTERNAL_SCORE
                && (bureauScore < MODERATE_BUREAU_SCORE || isGreaterOrEqual(defaultProbability, MODERATE_DEFAULT_PROBABILITY))) {
            decisionReasons.add("Низкий внутренний скор при дополнительных риск-факторах");
        }
    }

    private void collectManualReviewReasons(
            ScoringResponse scoring,
            Integer bureauScore,
            AntifraudResponse fraud,
            List<String> decisionReasons
    ) {
        BigDecimal defaultProbability = scoring.mlDefaultProbability();
        int antifraudScore = fraud.antifraudScore();
        int internalScore = scoring.internalScore();

        if (isGreaterOrEqual(defaultProbability, ELEVATED_DEFAULT_PROBABILITY)) {
            decisionReasons.add("Повышенный риск дефолта");
        }

        if (antifraudScore >= MEDIUM_ANTIFRAUD_SCORE) {
            decisionReasons.add("Средний антифрод риск");
        }

        if (bureauScore < BORDERLINE_BUREAU_SCORE) {
            decisionReasons.add("Пограничный БКИ скор");
        }

        if (internalScore < BORDERLINE_INTERNAL_SCORE) {
            decisionReasons.add("Низкий внутренний скор");
        }

        if (scoring.recommendedLimit() < MIN_APPROVED_LIMIT) {
            decisionReasons.add("Рекомендованный лимит ниже минимального порога");
        }

        if (scoring.scoringReasons().size() >= 4) {
            decisionReasons.add("Много негативных факторов внутреннего скоринга");
        }

        if (bureauScore < MODERATE_BUREAU_SCORE
                && isGreaterOrEqual(defaultProbability, MODERATE_DEFAULT_PROBABILITY)) {
            decisionReasons.add("Средний БКИ скор при заметном риске дефолта");
        }

        if (bureauScore < MODERATE_BUREAU_SCORE && antifraudScore >= ELEVATED_ANTIFRAUD_SCORE) {
            decisionReasons.add("Средний БКИ скор при повышенном антифрод риске");
        }

        if (internalScore < MODERATE_INTERNAL_SCORE
                && isGreaterOrEqual(defaultProbability, MODERATE_DEFAULT_PROBABILITY)) {
            decisionReasons.add("Средний внутренний скор при заметном риске дефолта");
        }
    }

    private int calculateApprovedLimit(ScoringResponse scoring, Integer bureauScore, AntifraudResponse fraud) {
        double multiplier = 1.0;

        if (isGreaterOrEqual(scoring.mlDefaultProbability(), MODERATE_DEFAULT_PROBABILITY)) {
            multiplier *= 0.80;
        }

        if (bureauScore < MODERATE_BUREAU_SCORE) {
            multiplier *= 0.85;
        }

        if (fraud.antifraudScore() >= ELEVATED_ANTIFRAUD_SCORE) {
            multiplier *= 0.90;
        }

        if (scoring.internalScore() < MODERATE_INTERNAL_SCORE) {
            multiplier *= 0.85;
        }

        int adjustedLimit = (int) Math.floor(scoring.recommendedLimit() * multiplier);
        return roundDownToStep(adjustedLimit, LIMIT_ROUNDING_STEP);
    }

    private int roundDownToStep(int value, int step) {
        return value / step * step;
    }

    private boolean isGreaterThan(BigDecimal value, BigDecimal threshold) {
        return value.compareTo(threshold) > 0;
    }

    private boolean isGreaterOrEqual(BigDecimal value, BigDecimal threshold) {
        return value.compareTo(threshold) >= 0;
    }
}
