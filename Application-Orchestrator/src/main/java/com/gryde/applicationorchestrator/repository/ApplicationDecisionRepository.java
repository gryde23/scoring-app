package com.gryde.applicationorchestrator.repository;

import com.gryde.applicationorchestrator.entity.Application;
import com.gryde.applicationorchestrator.entity.ApplicationDecision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ApplicationDecisionRepository extends JpaRepository<ApplicationDecision, UUID> {

    @Query(
            """
            select ad from ApplicationDecision ad
            where ad.application.user.id = :userId
            """
    )
    List<ApplicationDecision> findApplicationDecisionsByUserId(
            @Param("userId") UUID userId
    );

    @Query(
            """
            select ad from ApplicationDecision ad
            where ad.application.user.phone = :userPhone
            """
    )
    List<ApplicationDecision> findApplicationDecisionsByUserPhone(
            @Param("userPhone") String userPhone
    );

    @Query("""
            select ad from ApplicationDecision ad
            where ad.application.user.id = :userId
            and ad.createdAt >= current_date - 60
            """)
    List<ApplicationDecision> findDecisionsByUserIdForLastTwoMonth(
            @Param("userId") UUID userId);
}
