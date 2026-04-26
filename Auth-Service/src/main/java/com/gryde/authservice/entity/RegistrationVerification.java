package com.gryde.authservice.entity;

import com.gryde.authservice.dto.enums.CodeStatus;
import jakarta.persistence.*;
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

    @Column(name = "code_hash", nullable = false)
    private String code;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private CodeStatus status;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "attempts", nullable = false)
    private Integer attempts;

    @Column(name = "registration_token_hash")
    private String registrationToken;

    @Column(name = "registration_token_expires_at")
    private LocalDateTime registrationTokenExpiresAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        attempts = 0;
        createdAt = LocalDateTime.now();
        expiresAt = LocalDateTime.now().plusMinutes(5);
    }
}
