package com.gryde.authservice.repository;

import com.gryde.authservice.entity.KnownClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface KnownClientRepository extends JpaRepository<KnownClient, UUID> {

    @Query(
        """
        select kc from KnownClient kc
        where kc.phone = :phone
        and kc.isActive = true
        """
    )
    Optional<KnownClient> findActiveByPhone(
            @Param("phone") String phone);
}
