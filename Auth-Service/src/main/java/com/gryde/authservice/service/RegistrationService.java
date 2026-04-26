package com.gryde.authservice.service;

import com.gryde.authservice.dto.AuthResponse;
import com.gryde.authservice.dto.RegistrationRequest;
import com.gryde.authservice.dto.StartRegistrationRequest;
import com.gryde.authservice.dto.StartRegistrationResponse;
import com.gryde.authservice.dto.enums.CodeStatus;
import com.gryde.authservice.entity.KnownClient;
import com.gryde.authservice.entity.RegistrationVerification;
import com.gryde.authservice.entity.User;
import com.gryde.authservice.exception.UnverifiedPhoneException;
import com.gryde.authservice.exception.UserAlreadyExistsException;
import com.gryde.authservice.repository.KnownClientRepository;
import com.gryde.authservice.repository.RegistrationVerificationRepository;
import com.gryde.authservice.repository.UserRepository;
import com.gryde.authservice.security.JwtService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final KnownClientRepository knownClientRepository;
    private final UserRepository userRepository;
    private final VerificationService verificationService;
    private final RegistrationVerificationRepository verificationRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public StartRegistrationResponse startRegistration(StartRegistrationRequest request) {
        String phone = request.phone();
        KnownClient knownClient = knownClientRepository.findActiveByPhone(phone)
                .orElseThrow(() -> new NoSuchElementException("Client with phone " + phone + " not found"));

        if (userRepository.existsByPhone(phone)) {
            throw new IllegalArgumentException("Пользователь с таким номером уже зарегистрирован");
        }

        UUID verificationId = verificationService.createVerificationCode(phone, knownClient.getId());

        return new StartRegistrationResponse(verificationId.toString(),
                "Код верификации отправлен");
    }

    @Transactional
    public AuthResponse registerUser(RegistrationRequest request) {
        Claims claims = jwtService.parseRegistrationToken(request.registrationToken());
        UUID verificationId = jwtService.extractVerificationId(claims);

        RegistrationVerification verification = verificationRepository.findById(verificationId)
                .orElseThrow(() -> new NoSuchElementException("Verification with UUID: " + verificationId + " not found"));

        if (!verification.getStatus().equals(CodeStatus.VERIFIED)) {
            throw new UnverifiedPhoneException("Номер телефона не подтвержден.");
        }


        UUID userId = verification.getClientId();
        String phone = verification.getPhone();

        if (userRepository.existsByPhone(phone)) {
            throw new UserAlreadyExistsException("Пользователь с таким номером уже зарегистрирован");
        }

        String passwordHash = passwordEncoder.encode(request.password());

        User user = new User();
        user.setId(userId);
        user.setPassword(passwordHash);
        user.setPhone(phone);

        userRepository.save(user);

        verification.setStatus(CodeStatus.USED);
        verificationRepository.save(verification);

        String accessToken = jwtService.generateAccessToken(userId, "USER");

        return new AuthResponse(
                userId,
                accessToken,
                "Bearer",
                3600
        );
    }
}
