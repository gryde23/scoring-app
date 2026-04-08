package com.gryde.applicationorchestrator.repository;

import com.gryde.applicationorchestrator.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ApplicationRepository extends JpaRepository<Application, UUID> {

    @Query(
            """
            select a from Application a
            where a.user.id = :userId
            """
    )
    List<Application> findApplicationsByUserId(
           @Param("userId") UUID userId);

    @Query(
            """
            select a from Application a
            where a.user.phone = :userPhone
            """
    )
    List<Application> findApplicationsByUserPhone(
            @Param("userPhone") String userPhone);


    @Query("""
            select a from Application a
            where a.user.id = :userId
            and a.createdAt >= current_date - 60
            """)
    List<Application> findApplicationsByUserIdForLastTwoMonth(
            @Param("userId") UUID userId);
}
