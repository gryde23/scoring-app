package com.gryde.applicationorchestrator.controller;

import com.gryde.applicationorchestrator.dto.ApplicationCreateRequest;
import com.gryde.applicationorchestrator.dto.ServiceResponses;
import com.gryde.applicationorchestrator.service.OrchestratorService;
import com.gryde.contract.ApplicationDecisionDTO;
import com.gryde.contract.ScoringResponse;
import com.gryde.contract.enums.Decision;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/scoring")
public class OrchestratorController {

    private final OrchestratorService service;

    @PostMapping
    public ResponseEntity<ApplicationDecisionDTO> createApplication(
            @Valid @RequestBody ApplicationCreateRequest request
    ) {
        ServiceResponses responses = service.startScoring(request);
        ApplicationDecisionDTO decisionDTO = new ApplicationDecisionDTO(
                null,
                responses.bureauScore(),
                responses.scoringResponse().internalScore(),
                responses.scoringResponse().mlDefaultProbability(),
                responses.antifraudResponse().antifraudScore(),
                responses.antifraudResponse().antifraudFlags(),
                Decision.APPROVED,
                responses.scoringResponse().recommendedLimit(),
                responses.scoringResponse().scoringReasons(),
                LocalDateTime.now(),
                null
        );
        return ResponseEntity.status(HttpStatus.OK).body(decisionDTO);
    }
}
