package com.gryde.applicationorchestrator.repository;

import com.gryde.applicationorchestrator.entity.BureauSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BureauSnapshotRepository extends JpaRepository<BureauSnapshot, UUID> {
}
