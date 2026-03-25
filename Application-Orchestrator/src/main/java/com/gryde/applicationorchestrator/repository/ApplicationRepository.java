package com.gryde.applicationorchestrator.repository;

import com.gryde.applicationorchestrator.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ApplicationRepository extends JpaRepository<Application, UUID> {
}
