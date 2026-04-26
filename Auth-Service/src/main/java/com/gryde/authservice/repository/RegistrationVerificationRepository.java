package com.gryde.authservice.repository;

import com.gryde.authservice.entity.RegistrationVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RegistrationVerificationRepository extends JpaRepository<RegistrationVerification, UUID> {

    RegistrationVerification findFirstByPhoneOrderByCreatedAtDesc(String phone);
}
