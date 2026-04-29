package com.gryde.authservice.entity;

import com.gryde.authservice.dto.enums.VerificationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "registration_verifications")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegistrationVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "client_id", nullable = false)
    private UUID clientId;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private VerificationStatus status;

    @Column(name = "provider_check_id", nullable = false)
    private String providerCheckId;

    @Column(name = "call_phone", nullable = false)
    private String callPhone;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        expiresAt = LocalDateTime.now().plusMinutes(5);
    }
}
