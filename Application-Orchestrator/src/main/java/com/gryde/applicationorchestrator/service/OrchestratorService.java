package com.gryde.applicationorchestrator.service;

import com.gryde.applicationorchestrator.client.BureauClient;
import com.gryde.applicationorchestrator.client.ScoringClient;
import com.gryde.applicationorchestrator.dto.ApplicationCreateRequest;
import com.gryde.applicationorchestrator.dto.ApplicationDTO;
import com.gryde.applicationorchestrator.mapper.ApplicationMapper;
import com.gryde.contract.scoring.ScoringRequest;
import com.gryde.contract.scoring.ScoringResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrchestratorService {

    private final ApplicationService applicationService;
    private final ScoringClient scoringClient;
    private final BureauClient bureauClient;

    public ScoringResponse callScoring(ApplicationCreateRequest request) {
        ApplicationDTO dto = applicationService.createApplication(request);
        ScoringRequest scoringRequest = ApplicationMapper.toScoringRequest(dto);

        return scoringClient.calculate(scoringRequest);
    }

    public Integer callBureau(UUID userId) {
        return bureauClient.calculate(userId);
    }
}
