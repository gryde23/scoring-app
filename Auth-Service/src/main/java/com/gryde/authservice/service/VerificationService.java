package com.gryde.authservice.service;

import com.gryde.authservice.dto.StartRegistrationResponse;
import com.gryde.authservice.dto.VerificationResponse;
import com.gryde.authservice.dto.enums.CodeStatus;
import com.gryde.authservice.entity.RegistrationVerification;
import com.gryde.authservice.exception.CodeExpiredException;
import com.gryde.authservice.exception.MaxAttemptsExceededException;
import com.gryde.authservice.repository.RegistrationVerificationRepository;
import com.gryde.authservice.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificationService {

    private final RegistrationVerificationRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final Logger logger = LoggerFactory.getLogger(VerificationService.class);
    private static final SecureRandom secureRandom = new SecureRandom();
    private final JwtService jwtService;

    @Transactional
    public UUID createVerificationCode(String phone, UUID clientId) {
        RegistrationVerification lastCreatedCode = repository.findFirstByPhoneOrderByCreatedAtDesc(phone);

        if (lastCreatedCode != null &&
            LocalDateTime.now().isBefore(lastCreatedCode.getCreatedAt().plusSeconds(60))) {
            throw new IllegalArgumentException("Код может быть отправлен раз в 60 секунд");
        }

        String code = String.valueOf(secureRandom.nextInt(100_000, 1_000_000));
        String codeHash = passwordEncoder.encode(code);

        RegistrationVerification entity = new RegistrationVerification();
        entity.setPhone(phone);
        entity.setCode(codeHash);
        entity.setClientId(clientId);
        entity.setStatus(CodeStatus.CODE_SENT);

        repository.save(entity);
        logger.info("DEV ONLY: Код верификации для номера {}: {}", phone, code);

        return entity.getId();
    }


    public VerificationResponse verifyPhone(UUID verificationId, String code) {
        RegistrationVerification registrationVerification = repository.findById(verificationId)
                .orElseThrow(() -> new NoSuchElementException("Verification with UUID: " + verificationId + " not found"));


        if (registrationVerification.getExpiresAt().isBefore(LocalDateTime.now())) {
            registrationVerification.setStatus(CodeStatus.EXPIRED);
            repository.save(registrationVerification);
            throw new CodeExpiredException("Код верификации истёк.");
        }

        if (registrationVerification.getStatus().equals(CodeStatus.USED) ||
            registrationVerification.getStatus().equals(CodeStatus.VERIFIED)) {
            throw new IllegalStateException("Код верификации уже использован.");
        }

        if (registrationVerification.getAttempts() > 3) {
            throw new MaxAttemptsExceededException("Превышен лимит попыток верификации. Требуется создание нового кода.");
        }

        int attempts = registrationVerification.getAttempts() + 1;
        registrationVerification.setAttempts(attempts);

        if (!passwordEncoder.matches(code, registrationVerification.getCode())) {
            repository.save(registrationVerification);
            throw new IllegalArgumentException("Неверный код. Осталось попыток: " + (3 - attempts));
        } else {
            registrationVerification.setStatus(CodeStatus.VERIFIED);
            String registrationToken = jwtService.generateRegistrationToken(verificationId);

            return new VerificationResponse(registrationToken);
        }
    }
}
