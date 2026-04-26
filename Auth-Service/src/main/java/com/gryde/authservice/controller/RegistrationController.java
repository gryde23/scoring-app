package com.gryde.authservice.controller;

import com.gryde.authservice.dto.*;
import com.gryde.authservice.service.RegistrationService;
import com.gryde.authservice.service.VerificationService;
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
@RequestMapping("/api/auth/register")
public class RegistrationController {

    private final RegistrationService registrationService;
    private final VerificationService verificationService;

    @PostMapping("/start")
    public ResponseEntity<StartRegistrationResponse> startRegistration(
            @Valid @RequestBody StartRegistrationRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(registrationService.startRegistration(request));
    }

    @PostMapping("/verify-phone")
    public ResponseEntity<VerificationResponse> verifyPhone(
            @Valid @RequestBody VerificationRequest request
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(verificationService.verifyPhone(request));
    }

    @PostMapping("/complete")
    public ResponseEntity<AuthResponse> completeRegistration(
            @Valid @RequestBody RegistrationRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(registrationService.registerUser(request));
    }
}
