package com.gryde.applicationorchestrator.service;

import com.gryde.applicationorchestrator.client.AntifraudClient;
import com.gryde.applicationorchestrator.client.BureauClient;
import com.gryde.applicationorchestrator.client.ScoringClient;
import com.gryde.applicationorchestrator.dto.ApplicationCreateRequest;
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
    private final ScoringClient scoringClient;
    private final BureauClient bureauClient;
    private final AntifraudClient antifraudClient;

    public ScoringResponse callInternalScoring(ApplicationCreateRequest request) {

        return scoringClient.calculate(null);
    }

    public Integer callBureau(UUID userId) {
        return bureauClient.calculate(userId);
    }

    public AntifraudResponse callAntifraudCheck(UUID userId) {
        List<ApplicationResponse> applications = applicationService.findApplicationsByUserIdForLastMonth(userId);
        List<DecisionDTO> decisions = applicationDecisionService.findDecisionsByUserIdForLastMonth(userId);

        return antifraudClient.antifraudCheck(new AntifraudRequest(applications, decisions));
    }


    public DecisionDTO startScoring(ApplicationCreateRequest request, UUID userId) {
        ApplicationResponse application = applicationService.createApplication(request, userId);

        try {
            Integer bureauScore = callBureau(userId);
            ScoringResponse scoringResponse = callInternalScoring(request);
            AntifraudResponse antifraudResponse = callAntifraudCheck(userId);

            FinalDecision finalDecision = decisionEngine.decide(scoringResponse, bureauScore, antifraudResponse);

            DecisionDTO decision = applicationDecisionService.save(
                    application.id(), scoringResponse, bureauScore, antifraudResponse, finalDecision
            );
            applicationService.updateStatus(application.id(), ApplicationStatus.COMPLETED);

            return decision;
        } catch (Exception e) {
            applicationService.updateStatus(application.id(), ApplicationStatus.FAILED);
            throw e;
        }
    }
}
