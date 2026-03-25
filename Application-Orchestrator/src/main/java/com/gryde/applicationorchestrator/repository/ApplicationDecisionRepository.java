package com.gryde.applicationorchestrator.repository;

import com.gryde.applicationorchestrator.entity.ApplicationDecision;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ApplicationDecisionRepository extends JpaRepository<ApplicationDecision, UUID> {
}
