package com.gryde.applicationorchestrator.repository;

import com.gryde.applicationorchestrator.entity.Decision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface DecisionRepository extends JpaRepository<Decision, UUID> {

    @Query(
            """
            select ad from Decision ad
            where ad.application.userUUID = :userId
            """
    )
    List<Decision> findApplicationDecisionsByUserId(
            @Param("userId") UUID userId
    );


    @Query("""
            select ad from Decision ad
            where ad.application.userUUID = :userId
            and ad.createdAt >= :startDate
            """)
    List<Decision> findDecisionsByUserIdForLastMonth(
            @Param("userId") UUID userId,
            @Param("startDate") LocalDateTime startDate);
}
