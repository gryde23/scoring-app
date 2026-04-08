package com.gryde.applicationorchestrator.entity;

import com.gryde.applicationorchestrator.converter.*;
import com.gryde.contract.enums.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "applications")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "age", nullable = false)
    private Integer age;

    @Column(name = "gender", nullable = false)
    private char gender;

    @Column(name = "marital_status", nullable = false)
    @Convert(converter = MaritalStatusConverter.class)
    private MaritalStatus maritalStatus;

    private Integer dependents;

    @Column(name = "education", nullable = false)
    @Convert(converter = EducationConverter.class)
    private Education education;

    @Column(name = "region", nullable = false)
    @Convert(converter = RegionConverter.class)
    private Region region;

    @Column(name = "employment_type", nullable = false)
    @Convert(converter = EmploymentTypeConverter.class)
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

    @Column(name = "existing_cards")
    private Integer existingCards;

    @Column(name = "existing_loans")
    private Integer existingLoans;

    @Column(name = "total_monthly_debt")
    private Integer totalMonthlyDebt;

    @Column(name = "has_salary_project", nullable = false)
    private boolean hasSalaryProject;

    @Column(name = "has_deposit", nullable = false)
    private boolean hasDeposit;

    @Column(name = "card_type_requested", nullable = false)
    @Convert(converter = CardTypeConverter.class)
    private CardType cardTypeRequested;

    @Column(name = "status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private ApplicationStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(mappedBy = "application", cascade = CascadeType.ALL)
    private ApplicationDecision decision;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
