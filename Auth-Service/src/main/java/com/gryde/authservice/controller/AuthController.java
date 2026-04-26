package com.gryde.authservice.controller;

import com.gryde.authservice.dto.AuthResponse;
import com.gryde.authservice.dto.LoginRequest;
import com.gryde.authservice.service.AuthService;
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
@RequestMapping("/api/auth/login")
public class AuthController {

    private final AuthService authService;

    @PostMapping
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(authService.login(request));
    }
}
