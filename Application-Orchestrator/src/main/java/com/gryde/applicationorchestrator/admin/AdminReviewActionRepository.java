package com.gryde.applicationorchestrator.admin;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AdminReviewActionRepository extends JpaRepository<AdminReviewAction, UUID> {
}
