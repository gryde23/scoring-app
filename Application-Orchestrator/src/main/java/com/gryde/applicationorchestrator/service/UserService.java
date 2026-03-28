package com.gryde.applicationorchestrator.service;

import com.gryde.applicationorchestrator.dto.CreateUserRequest;
import com.gryde.applicationorchestrator.dto.UserResponse;
import com.gryde.applicationorchestrator.entity.User;
import com.gryde.applicationorchestrator.mapper.UserMapper;
import com.gryde.applicationorchestrator.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserResponse createUser(CreateUserRequest request) {
        if (request.email() == null && request.phone() == null) {
            throw new IllegalArgumentException("Cannot create user with empty phone and email");
        }

        User entity = new User();

        entity.setEmail(request.email());
        entity.setPhone(request.phone());

        User saved = userRepository.save(entity);
        return userMapper.toUserResponse(saved);
    }

    public UserResponse findUserByPhoneOrEmail(String phone, String email) {
        User user;

        if (phone != null) {
            user = userRepository.findUserByPhone(phone);
            System.out.println("Found user with UUID: " + user.getId() + " phone: " + user.getPhone());
        } else {
            user = userRepository.findUserByEmail(email);
            System.out.println("Found user with UUID: " + user.getId()+ " email: " + user.getEmail());
        }

        return userMapper.toUserResponse(user);
    }


}
