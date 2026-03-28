package com.gryde.applicationorchestrator.controller;

import com.gryde.applicationorchestrator.dto.CreateUserRequest;
import com.gryde.applicationorchestrator.dto.UserResponse;
import com.gryde.applicationorchestrator.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody CreateUserRequest request) {
        logger.info("CreateUserRequest with phone: {} email: {}", request.phone(), request.email());
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<UserResponse> getUserByPhoneOrEmail(
            @RequestParam(name = "phone", required = false) String phone,
            @RequestParam(name = "email", required = false) String email
    ) {
        logger.info("Get user by phone: {} or email: {}", phone, email);
        if (phone == null && email == null) {
            throw new IllegalArgumentException("Phone or email must be provided");
        }

        if (phone != null && email != null) {
            throw new IllegalArgumentException("Only one parameter should be provided");
        }

        UserResponse response = userService.findUserByPhoneOrEmail(phone, email);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
