package com.gryde.authservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "verification_codes")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class VerificationCode {

    @Id
    private UUID id;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "used", nullable = false)
    private boolean used;

    @Column(name = "attempts", nullable = false)
    private Integer attempts;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        used = false;
        attempts = 3;
        createdAt = LocalDateTime.now();
        expiresAt = LocalDateTime.now().plusMinutes(10);
    }
}
