package com.gryde.applicationorchestrator.repository;

import com.gryde.applicationorchestrator.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ApplicationRepository extends JpaRepository<Application, UUID> {

    @Query(
            """
            select a from Application a
            where a.userUUID = :userId
            """
    )
    List<Application> findApplicationsByUserId(
           @Param("userId") UUID userId);


    @Query("""
            select a from Application a
            where a.userUUID = :userId
            and a.createdAt >= :startDate
            """)
    List<Application> findApplicationsByUserIdForLastMonth(
            @Param("userId") UUID userId,
            @Param("startDate") LocalDate startDate);
}
