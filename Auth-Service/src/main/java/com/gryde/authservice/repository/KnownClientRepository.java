package com.gryde.authservice.repository;

import com.gryde.authservice.entity.KnownClient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface KnownClientRepository extends JpaRepository<KnownClient, UUID> {

    boolean existsByPhoneAndActiveIsTrue(String phone);
}
