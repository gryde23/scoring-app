package com.gryde.applicationorchestrator.repository;

import com.gryde.applicationorchestrator.entity.Application;
import com.gryde.contract.enums.FinalDecision;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ApplicationRepository extends JpaRepository<Application, UUID> {

    @Query(
            """
            select a from Application a
            where a.userId = :userId
            """
    )
    List<Application> findAllByUserIdOrderByCreatedAtDesc(
           @Param("userId") UUID userId);


    @Query("""
            select a from Application a
            where a.userId = :userId
            and a.createdAt >= :startDate
            and a.status <> 'IN_PROGRESS'
            """)
    List<Application> findApplicationsByUserIdForLastMonth(
            @Param("userId") UUID userId,
            @Param("startDate") LocalDateTime startDate);

    @Query("""
            select a from Application a
            where a.decision.finalDecision = :decision
            """
    )
    Page<Application> findAllByDecision(FinalDecision decision, Pageable pageable);
}
