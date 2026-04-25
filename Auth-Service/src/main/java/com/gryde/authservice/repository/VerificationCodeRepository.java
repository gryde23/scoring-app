package com.gryde.authservice.repository;

import com.gryde.authservice.entity.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, UUID> {
}
