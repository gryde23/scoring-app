package com.gryde.applicationorchestrator.controller;

import com.gryde.applicationorchestrator.dto.ApplicationCreateRequest;
import com.gryde.applicationorchestrator.dto.ApplicationDTO;
import com.gryde.applicationorchestrator.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<List<ApplicationDTO>> getUserApplicationsByPhoneOrEmail(
            @RequestParam(name = "phone", required = false) String phone,
            @RequestParam(name = "email", required = false) String email
    ) {
        logger.info("Get user applications by phone: {} or email: {}", phone, email);
        if (phone == null && email == null) {
            throw new IllegalArgumentException("Phone or email must be provided");
        }

        if (phone != null && email != null) {
            throw new IllegalArgumentException("Only one parameter should be provided");
        }

        List<ApplicationDTO> applications = applicationService.findUserApplicationsByPhoneOrEmail(phone, email);

        return ResponseEntity.status(HttpStatus.OK).body(applications);
    }
}
