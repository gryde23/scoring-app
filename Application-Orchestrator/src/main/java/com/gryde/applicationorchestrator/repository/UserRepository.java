package com.gryde.applicationorchestrator.repository;

import com.gryde.applicationorchestrator.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    User findUserByPhone(String phone);
}
