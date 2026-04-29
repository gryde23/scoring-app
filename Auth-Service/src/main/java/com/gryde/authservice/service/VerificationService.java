package com.gryde.authservice.service;

import com.gryde.authservice.dto.SmsRuCallCheckAddResponse;
import com.gryde.authservice.dto.SmsRuCallCheckStatusResponse;
import com.gryde.authservice.dto.StartRegistrationResponse;
import com.gryde.authservice.dto.VerificationRequest;
import com.gryde.authservice.dto.VerificationResponse;
import com.gryde.authservice.dto.enums.VerificationStatus;
import com.gryde.authservice.entity.RegistrationVerification;
import com.gryde.authservice.exception.CodeExpiredException;
import com.gryde.authservice.exception.SmsRuCallCheckException;
import com.gryde.authservice.repository.RegistrationVerificationRepository;
import com.gryde.authservice.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificationService {

    private static final String SMS_RU_WAITING_STATUS = "400";
    private static final String SMS_RU_VERIFIED_STATUS = "401";
    private static final String SMS_RU_EXPIRED_STATUS = "402";

    private final RegistrationVerificationRepository repository;
    private final SmsRuCallCheckClient smsRuCallCheckClient;
    private final JwtService jwtService;

    @Transactional
    public StartRegistrationResponse createCallVerification(String phone, UUID clientId) {
        RegistrationVerification lastVerification = repository.findFirstByPhoneOrderByCreatedAtDesc(phone);

        if (lastVerification != null &&
            LocalDateTime.now().isBefore(lastVerification.getCreatedAt().plusSeconds(60))) {
            throw new IllegalArgumentException("Повторную проверку можно создать раз в 60 секунд");
        }

        SmsRuCallCheckAddResponse providerResponse = smsRuCallCheckClient.startVerification(phone);

        RegistrationVerification entity = new RegistrationVerification();
        entity.setPhone(phone);
        entity.setClientId(clientId);
        entity.setStatus(VerificationStatus.PENDING);
        entity.setProviderCheckId(providerResponse.check_id());
        entity.setCallPhone(providerResponse.call_phone());

        RegistrationVerification saved = repository.save(entity);

        return new StartRegistrationResponse(
                saved.getId().toString(),
                providerResponse.call_phone(),
                providerResponse.call_phone_pretty(),
                saved.getExpiresAt(),
                "Позвоните на указанный номер для подтверждения телефона"
        );
    }

    @Transactional
    public VerificationResponse verifyPhone(VerificationRequest request) {
        UUID verificationId = request.verificationId();

        RegistrationVerification registrationVerification = repository.findById(verificationId)
                .orElseThrow(() -> new NoSuchElementException("Verification with UUID: " + verificationId + " not found"));

        if (VerificationStatus.USED.equals(registrationVerification.getStatus())) {
            throw new IllegalStateException("Проверка телефона уже использована.");
        }

        if (VerificationStatus.VERIFIED.equals(registrationVerification.getStatus())) {
            String registrationToken = jwtService.generateRegistrationToken(verificationId);
            return new VerificationResponse(VerificationStatus.VERIFIED, registrationToken);
        }

        if (registrationVerification.getExpiresAt().isBefore(LocalDateTime.now())) {
            registrationVerification.setStatus(VerificationStatus.EXPIRED);
            repository.save(registrationVerification);
            throw new CodeExpiredException("Время проверки звонком истекло.");
        }

        SmsRuCallCheckStatusResponse providerStatus =
                smsRuCallCheckClient.getStatus(registrationVerification.getProviderCheckId());

        String checkStatus = providerStatus.check_status();

        if (SMS_RU_WAITING_STATUS.equals(checkStatus)) {
            return new VerificationResponse(VerificationStatus.PENDING, null);
        }

        if (SMS_RU_EXPIRED_STATUS.equals(checkStatus)) {
            registrationVerification.setStatus(VerificationStatus.EXPIRED);
            repository.save(registrationVerification);
            throw new CodeExpiredException("Время проверки звонком истекло.");
        }

        if (SMS_RU_VERIFIED_STATUS.equals(checkStatus)) {
            registrationVerification.setStatus(VerificationStatus.VERIFIED);
            repository.save(registrationVerification);

            String registrationToken = jwtService.generateRegistrationToken(verificationId);
            return new VerificationResponse(VerificationStatus.VERIFIED, registrationToken);
        }

        throw new SmsRuCallCheckException("Неожиданный статус проверки звонком: " + checkStatus);
    }
}
