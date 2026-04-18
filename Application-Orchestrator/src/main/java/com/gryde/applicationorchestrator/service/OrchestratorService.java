package com.gryde.applicationorchestrator.service;

import com.gryde.applicationorchestrator.client.AntifraudClient;
import com.gryde.applicationorchestrator.client.BureauClient;
import com.gryde.applicationorchestrator.client.ScoringClient;
import com.gryde.applicationorchestrator.dto.ApplicationCreateRequest;
import com.gryde.applicationorchestrator.entity.Application;
import com.gryde.contract.*;
import com.gryde.applicationorchestrator.mapper.ApplicationMapper;
import com.gryde.contract.enums.ApplicationStatus;
import com.gryde.contract.enums.FinalDecision;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrchestratorService {

    private final ApplicationService applicationService;
    private final DecisionService applicationDecisionService;
    private final DecisionEngine decisionEngine;
    private final BureauSnapshotService bureauSnapshotService;
    private final ScoringClient scoringClient;
    private final BureauClient bureauClient;
    private final AntifraudClient antifraudClient;

    public ScoringResponse callInternalScoring(ApplicationCreateRequest request, BureauDataResponse bureauData) {
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
                bureauData.monthlyDebtPayment()
        );

        return scoringClient.calculate(scoringRequest);
    }

    public BureauDataResponse callBureau(UUID userId, ApplicationResponse application) {
        BureauDataResponse bureauResponse = bureauClient.calculate(userId);
        bureauSnapshotService.saveSnapshot(application.id(), bureauResponse);

        return bureauResponse;
    }

    public AntifraudResponse callAntifraudCheck(UUID userId) {
        List<ApplicationResponse> applications = applicationService.findApplicationsByUserIdForLastMonth(userId);
        List<DecisionDTO> decisions = applicationDecisionService.findDecisionsByUserIdForLastMonth(userId);

        return antifraudClient.antifraudCheck(new AntifraudRequest(applications, decisions));
    }


    public DecisionDTO startScoring(ApplicationCreateRequest request, UUID userId) {
        ApplicationResponse application = applicationService.createApplication(request, userId);

        try {
            BureauDataResponse bureauResponse = callBureau(userId, application);
            ScoringResponse scoringResponse = callInternalScoring(request, bureauResponse);
            AntifraudResponse antifraudResponse = callAntifraudCheck(userId);

            FinalDecision finalDecision = decisionEngine.decide(scoringResponse, bureauResponse.bureauScore(), antifraudResponse);

            DecisionDTO decision = applicationDecisionService.save(
                    application.id(), scoringResponse, bureauResponse.bureauScore(), antifraudResponse, finalDecision
            );
            applicationService.updateStatus(application.id(), ApplicationStatus.COMPLETED);

            return decision;
        } catch (Exception e) {
            applicationService.updateStatus(application.id(), ApplicationStatus.FAILED);
            throw e;
        }
    }
}
