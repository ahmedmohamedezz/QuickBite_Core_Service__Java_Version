package com.quickbite.core.auth.service;

import com.quickbite.core.auth.exception.CannotSignupAsSystemAdminException;
import com.quickbite.core.auth.exception.UserAlreadyExistsException;
import com.quickbite.core.auth.exception.InvalidCredentialsException;
import com.quickbite.core.common.security.JwtService;
import com.quickbite.core.user.domain.UserEntity;
import com.quickbite.core.auth.dto.AuthResponse;
import com.quickbite.core.auth.dto.UserLoginRequest;
import com.quickbite.core.auth.dto.UserRegisterRequest;
import com.quickbite.core.auth.dto.UserResponse;
import com.quickbite.core.user.enums.SystemRole;
import com.quickbite.core.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(UserRegisterRequest request) {
        // system admins are configured manually
        if (request.getRole() == SystemRole.system_admin) {
            throw new CannotSignupAsSystemAdminException();
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException();
        }

        if (userRepository.existsByPhone(request.getPhone())) {
            throw new UserAlreadyExistsException();
        }

        // 3. Build and hash user credentials
        UserEntity user = new UserEntity();
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setName(request.getName());
        user.setSystemRole(request.getRole());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        UserEntity savedUser = userRepository.save(user);

        // 4. Generate individual token signatures
        String accessToken = jwtService.generateAccessToken(
                user.getId(), user.getEmail(), String.valueOf(user.getSystemRole()));
        String refreshToken = jwtService.generateRefreshToken(
                user.getId(), user.getEmail(), String.valueOf(user.getSystemRole()));

        return AuthResponse.builder()
                .message("Register successful")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(UserResponse.fromEntity(savedUser))
                .build();
    }

    @Transactional(readOnly = true)
    public AuthResponse login(UserLoginRequest request) {
        // 1. Find active user record
        UserEntity user = userRepository.findActiveByEmail(request.getEmail())
                .orElseThrow(InvalidCredentialsException::new);

        // 2. Verify hashed password compatibility safely
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        // 3. Generate individual token signatures
        String accessToken = jwtService.generateAccessToken(
                user.getId(), user.getEmail(), String.valueOf(user.getSystemRole()));
        String refreshToken = jwtService.generateRefreshToken(
                user.getId(), user.getEmail(), String.valueOf(user.getSystemRole()));

        return AuthResponse.builder()
                .message("Login successful")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(UserResponse.fromEntity(user))
                .build();
    }
}