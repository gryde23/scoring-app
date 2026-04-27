package com.gryde.applicationorchestrator.controller;

import com.gryde.applicationorchestrator.dto.ApplicationShortResponse;
import com.gryde.applicationorchestrator.dto.ApplicationWithDecisionResponse;
import com.gryde.applicationorchestrator.service.ApplicationService;
import com.gryde.contract.ApplicationResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationWithDecisionResponse> getApplicationWithDecision(
            @PathVariable("id") UUID applicationId,
            Authentication authentication
    ) {
        UUID userId = (UUID) authentication.getPrincipal();

        ApplicationWithDecisionResponse response = applicationService.getApplicationWithDecision(applicationId, userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<ApplicationShortResponse>> getUserApplications(
            Authentication authentication
    ) {
        UUID userId = (UUID) authentication.getPrincipal();

        return ResponseEntity.status(HttpStatus.OK)
                .body(applicationService.getApplicationsByUserId(userId));
    }
}
