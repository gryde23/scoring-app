package com.gryde.applicationorchestrator.entity;

import com.gryde.applicationorchestrator.enums.Decision;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "application_decisions")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ApplicationDecision {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "bureau_score")
    private Integer bureauScore;

    @Column(name = "internal_score", nullable = false)
    private Integer internalScore;

    @Column(name = "ml_default_probability")
    private BigDecimal mlDefaultProbability;

    @Column(name = "antifraud_score")
    private Integer antifraudScore;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "antifraud_flags", columnDefinition = "jsonb")
    private List<String> antifraudFlags;

    @Column(name = "final_decision", nullable = false)
    @Enumerated(EnumType.STRING)
    private Decision finalDecision;

    @Column(name = "approved_limit")
    private Integer approvedLimit;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "decision_reasons", columnDefinition = "jsonb")
    private List<String> decisionReasons;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToOne
    @JoinColumn(name = "application_id", nullable = false, unique = true)
    private Application application;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
