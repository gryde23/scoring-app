package com.gryde.bureauservice.repository;

import com.gryde.bureauservice.entity.CreditAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CreditAccountRepository extends JpaRepository<CreditAccount, UUID> {
}
