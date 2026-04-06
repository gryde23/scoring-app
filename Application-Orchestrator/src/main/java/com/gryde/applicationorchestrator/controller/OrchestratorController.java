package com.gryde.applicationorchestrator.controller;

import com.gryde.applicationorchestrator.dto.ApplicationCreateRequest;
import com.gryde.applicationorchestrator.dto.ApplicationDTO;
import com.gryde.applicationorchestrator.service.OrchestratorService;
import com.gryde.contract.scoring.ScoringResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/scoring")
public class OrchestratorController {

    private final OrchestratorService service;

    @PostMapping
    public ResponseEntity<ScoringResponse> createApplication(
            @Valid @RequestBody ApplicationCreateRequest request
    ) {
        Integer bureauScore = service.callBureau(request.userUUID());
        System.out.println("BUREAU SCORE: " + bureauScore);
        ScoringResponse scoringResponse = service.callScoring(request);

        return ResponseEntity.status(HttpStatus.OK).body(scoringResponse);
    }
}
