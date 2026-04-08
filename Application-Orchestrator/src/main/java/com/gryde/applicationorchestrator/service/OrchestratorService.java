package com.gryde.applicationorchestrator.service;

import com.gryde.applicationorchestrator.client.AntifraudClient;
import com.gryde.applicationorchestrator.client.BureauClient;
import com.gryde.applicationorchestrator.client.ScoringClient;
import com.gryde.applicationorchestrator.dto.ApplicationCreateRequest;
import com.gryde.applicationorchestrator.dto.ServiceResponses;
import com.gryde.contract.*;
import com.gryde.applicationorchestrator.mapper.ApplicationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrchestratorService {

    private final ApplicationService applicationService;
    private final ApplicationDecisionService applicationDecisionService;
    private final ScoringClient scoringClient;
    private final BureauClient bureauClient;
    private final AntifraudClient antifraudClient;

    public ScoringResponse callInternalScoring(ApplicationCreateRequest request) {
        ApplicationDTO dto = applicationService.createApplication(request);
        ScoringRequest scoringRequest = ApplicationMapper.toScoringRequest(dto);

        return scoringClient.calculate(scoringRequest);
    }

    public Integer callBureau(UUID userId) {
        return bureauClient.calculate(userId);
    }

    public AntifraudResponse callAntifraudCheck(UUID userId) {
        List<ApplicationDTO> applications = applicationService.findApplicationsByUserIdForLastTwoMonth(userId);
        List<ApplicationDecisionDTO> decisions = applicationDecisionService.findDecisionsByUserIdForLastTwoMonth(userId);

        return antifraudClient.antifraudCheck(new AntifraudRequest(applications, decisions));
    }

    public ServiceResponses startScoring(ApplicationCreateRequest request) {
        Integer bureauScore = callBureau(request.userUUID());
        ScoringResponse scoringResponse = callInternalScoring(request);
        AntifraudResponse antifraudResponse = callAntifraudCheck(request.userUUID());

        return new ServiceResponses(scoringResponse, bureauScore, antifraudResponse);
    }
}
