package com.gryde.applicationorchestrator.controller;

import com.gryde.applicationorchestrator.dto.ApplicationCreateRequest;
import com.gryde.contract.ApplicationDTO;
import com.gryde.applicationorchestrator.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final Logger logger = LoggerFactory.getLogger(ApplicationController.class);

    @PostMapping
    public ResponseEntity<ApplicationDTO> createApplication(
            @Valid @RequestBody ApplicationCreateRequest request
            ) {
        logger.info("Request to create application: {}", request);

        ApplicationDTO applicationDTO = applicationService.createApplication(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(applicationDTO);
    }

    @GetMapping
    public ResponseEntity<List<ApplicationDTO>> getUserApplicationsByPhone(
            @RequestParam(name = "phone") String phone
    ) {
        logger.info("Get user applications by phone: {}", phone);

        List<ApplicationDTO> applications = applicationService.findUserApplicationsByPhone(phone);

        return ResponseEntity.status(HttpStatus.OK).body(applications);
    }

    @GetMapping("/antifraud/{id}")
    public ResponseEntity<List<ApplicationDTO>> getLastTwoMonthApplications(
            @PathVariable(name = "id") UUID userId
            ) {
        List<ApplicationDTO> applications = applicationService.findApplicationsByUserIdForLastTwoMonth(userId);

        return ResponseEntity.ok(applications);
    }

}
