package com.gryde.applicationorchestrator.controller;

import com.gryde.applicationorchestrator.dto.ApplicationCreateRequest;
import com.gryde.applicationorchestrator.service.OrchestratorService;
import com.gryde.contract.DecisionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/scoring")
public class OrchestratorController {

    private final OrchestratorService service;

    @PostMapping
    public ResponseEntity<DecisionResponse> createApplication(
            @RequestBody ApplicationCreateRequest request,
            Authentication authentication
    ) {
        UUID userId = (UUID) authentication.getPrincipal();

        DecisionResponse decisionResponse = service.startScoring(request, userId);
        return ResponseEntity.status(HttpStatus.OK).body(decisionResponse);
    }
}
