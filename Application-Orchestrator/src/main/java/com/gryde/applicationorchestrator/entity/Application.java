package com.gryde.applicationorchestrator.entity;

import com.gryde.contract.enums.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "applications")
@Data
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userUUID;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "age", nullable = false)
    private Integer age;

    @Column(name = "marital_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private MaritalStatus maritalStatus;

    private Integer dependents;

    @Column(name = "education", nullable = false)
    @Enumerated(EnumType.STRING)
    private Education education;

    @Column(name = "region", nullable = false)
    @Enumerated(EnumType.STRING)
    private Region region;

    @Column(name = "employment_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private EmploymentType employmentType;

    @Column(name = "employment_length")
    private Integer employmentLength;

    @Column(name = "monthly_income", nullable = false)
    private Integer monthlyIncome;

    @Column(name = "additional_income")
    private Integer additionalIncome;

    @Column(name = "has_property", nullable = false)
    private boolean hasProperty;

    @Column(name = "has_car", nullable = false)
    private boolean hasCar;

    @Column(name = "has_salary_project", nullable = false)
    private boolean hasSalaryProject;

    @Column(name = "has_deposit", nullable = false)
    private boolean hasDeposit;

    @Column(name = "card_type_requested", nullable = false)
    @Enumerated(EnumType.STRING)
    private CardType cardTypeRequested;

    @Column(name = "status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private ApplicationStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private Decision decision;

    @OneToOne(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private BureauSnapshot bureauSnapshot;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
