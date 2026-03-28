package com.gryde.applicationorchestrator.service;

import com.gryde.applicationorchestrator.controller.UserController;
import com.gryde.applicationorchestrator.dto.CreateUserRequest;
import com.gryde.applicationorchestrator.dto.UserResponse;
import com.gryde.applicationorchestrator.entity.User;
import com.gryde.applicationorchestrator.mapper.UserMapper;
import com.gryde.applicationorchestrator.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserResponse createUser(CreateUserRequest request) {
        if (request.email() == null && request.phone() == null) {
            throw new IllegalArgumentException("Cannot create user with empty phone and email");
        }

        User entity = new User();

        entity.setEmail(request.email());
        entity.setPhone(request.phone());

        User saved = userRepository.save(entity);
        logger.info("Saved user: {}", saved);
        return userMapper.toUserResponse(saved);
    }

    public UserResponse findUserByPhoneOrEmail(String phone, String email) {
        User user;

        if (phone != null) {
            user = userRepository.findUserByPhone(phone);
            logger.info("Found user by phone: {}", user);
        } else {
            user = userRepository.findUserByEmail(email);
            logger.info("Found user by email: {}", user);
        }

        return userMapper.toUserResponse(user);
    }


}
