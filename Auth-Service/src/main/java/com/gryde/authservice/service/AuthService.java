package com.gryde.authservice.service;

import com.gryde.authservice.dto.AuthResponse;
import com.gryde.authservice.dto.LoginRequest;
import com.gryde.authservice.entity.User;
import com.gryde.authservice.exception.IncorrectPasswordException;
import com.gryde.authservice.exception.UserNotFoundException;
import com.gryde.authservice.repository.UserRepository;
import com.gryde.authservice.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse login(LoginRequest request) {
        String phone = request.phone();
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с номером " + phone + " не найден"));

        String password = request.password();

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IncorrectPasswordException("Неверный пароль.");
        } else {
            String token = jwtService.generateAccessToken(user.getId(), "USER");

            return new AuthResponse(
                    user.getId(),
                    token,
                    "Bearer",
                    3600
            );
        }
    }
}
