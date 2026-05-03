package com.gryde.applicationorchestrator.service;

import com.gryde.applicationorchestrator.client.AntifraudClient;
import com.gryde.applicationorchestrator.client.BureauClient;
import com.gryde.applicationorchestrator.client.ScoringClient;
import com.gryde.applicationorchestrator.dto.ApplicationCreateRequest;
import com.gryde.applicationorchestrator.dto.DecisionResult;
import com.gryde.contract.*;
import com.gryde.contract.enums.ApplicationStatus;
import com.gryde.contract.enums.FinalDecision;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrchestratorService {
    private static final int EARLY_REJECT_ANTIFRAUD_SCORE = 500;

    private final ApplicationService applicationService;
    private final DecisionService applicationDecisionService;
    private final DecisionEngine decisionEngine;
    private final BureauSnapshotService bureauSnapshotService;
    private final ScoringClient scoringClient;
    private final BureauClient bureauClient;
    private final AntifraudClient antifraudClient;

    public ScoringResponse callInternalScoring(ApplicationCreateRequest request, BureauSnapshotResponse bureauData) {
        BigDecimal totalIncome = BigDecimal.valueOf(request.monthlyIncome() + request.additionalIncome());
        BigDecimal debtToIncome = totalIncome.divide(totalIncome, 4, RoundingMode.HALF_UP);
        ScoringRequest scoringRequest = new ScoringRequest(
                request.age(),
                request.maritalStatus().name().toLowerCase(),
                request.dependents(),
                request.education().name().toLowerCase(),
                request.region().name().toLowerCase(),
                request.employmentType().name().toLowerCase(),
                request.employmentLength(),
                request.monthlyIncome(),
                request.additionalIncome(),
                request.hasProperty(),
                request.hasCar(),
                request.hasSalaryProject(),
                request.hasDeposit(),
                request.cardTypeRequested().name().toLowerCase(),
                bureauData.totalAccounts(),
                bureauData.activeAccounts(),
                bureauData.closedAccounts(),
                bureauData.defaultAccounts(),
                bureauData.restructuredAccounts(),
                bureauData.creditHistoryDays(),
                bureauData.totalCreditLimit(),
                bureauData.totalActiveDebt(),
                bureauData.utilizationRatio(),
                bureauData.totalPayments(),
                bureauData.dpd30(),
                bureauData.dpd60(),
                bureauData.dpd90(),
                bureauData.dpd90Plus(),
                bureauData.maxDaysOverdue(),
                bureauData.paymentRatio(),
                bureauData.partialPaymentsCount(),
                bureauData.recentOverdueCount(),
                bureauData.monthlyDebtPayment(),
                debtToIncome
        );

        return scoringClient.calculate(scoringRequest);
    }

    public BureauResultResponse callBureau(UUID userId, ApplicationResponse application) {
        BureauResultResponse bureauResponse = bureauClient.calculate(userId);
        if (bureauResponse.bureauData() != null) {
            bureauSnapshotService.saveSnapshot(application.id(), bureauResponse.bureauData());
        }

        return bureauResponse;
    }

    public AntifraudResponse callAntifraudCheck(UUID userId, BureauSnapshotResponse bureauSnapshot, ApplicationResponse newApplication) {
        List<ApplicationResponse> applications = applicationService.findCompletedApplicationsByUserIdForLastMonth(userId);
        List<DecisionResponse> decisions = applicationDecisionService.findDecisionsByUserIdForLastMonth(userId);

        return antifraudClient.antifraudCheck(new AntifraudRequest(newApplication, applications, decisions, bureauSnapshot));
    }


    public DecisionResponse startScoring(ApplicationCreateRequest request, UUID userId) {
        ApplicationResponse application = applicationService.createApplication(request, userId);

        try {
            BureauResultResponse bureauResponse = callBureau(userId, application);
            if (bureauResponse.selfBanned()) {
                DecisionResponse decision = applicationDecisionService.saveEarlyRejection(
                        application.id(),
                        null,
                        null,
                        List.of("Установлен самозапрет на кредитование")
                );
                applicationService.updateStatus(application.id(), ApplicationStatus.COMPLETED);
                return decision;
            }

            BureauSnapshotResponse bureauData = bureauResponse.bureauData();
            AntifraudResponse antifraudResponse = callAntifraudCheck(userId, bureauData, application);
            if (antifraudResponse.antifraudScore() > EARLY_REJECT_ANTIFRAUD_SCORE) {
                DecisionResponse decision = applicationDecisionService.saveEarlyRejection(
                        application.id(),
                        bureauData.bureauScore(),
                        antifraudResponse,
                        List.of("Подозрительная заявка")
                );
                applicationService.updateStatus(application.id(), ApplicationStatus.COMPLETED);
                return decision;
            }

            ScoringResponse scoringResponse = callInternalScoring(request, bureauData);


            DecisionResult decisionResult = decisionEngine.decide(scoringResponse, bureauData.bureauScore(), antifraudResponse);

            DecisionResponse decision = applicationDecisionService.save(
                    application.id(), scoringResponse, bureauData.bureauScore(), antifraudResponse, decisionResult
            );

            if (!decision.finalDecision().equals(FinalDecision.MANUAL_REVIEW)) {
                applicationService.updateStatus(application.id(), ApplicationStatus.COMPLETED);
            }

            return decision;
        } catch (Exception e) {
            applicationService.updateStatus(application.id(), ApplicationStatus.FAILED);
            throw e;
        }
    }
}
