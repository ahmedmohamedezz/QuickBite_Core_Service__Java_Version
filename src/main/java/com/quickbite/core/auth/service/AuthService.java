package com.quickbite.core.auth.service;

import com.quickbite.core.auth.AuthUtils;
import com.quickbite.core.auth.domain.PasswordResetsEntity;
import com.quickbite.core.auth.dto.*;
import com.quickbite.core.auth.exception.CannotSignupAsSystemAdminException;
import com.quickbite.core.auth.exception.UserAlreadyExistsException;
import com.quickbite.core.auth.exception.InvalidCredentialsException;
import com.quickbite.core.auth.repository.PasswordResetRepository;
import com.quickbite.core.common.security.JwtService;
import com.quickbite.core.user.domain.UserEntity;
import com.quickbite.core.user.enums.SystemRole;
import com.quickbite.core.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordResetRepository passwordResetRepository;
    private final AuthUtils authUtils;

    public AuthService(
            UserRepository userRepository,
            PasswordResetRepository passwordResetRepository,
            AuthUtils authUtils) {
        this.userRepository = userRepository;
        this.passwordResetRepository = passwordResetRepository;
        this.authUtils = authUtils;
    }

    @Transactional
    public AuthResponse register(UserRegisterDto data) {
        // system admins are configured manually
        if (data.getRole() == SystemRole.system_admin) {
            throw new CannotSignupAsSystemAdminException();
        }

        if (userRepository.existsByEmail(data.getEmail())) {
            throw new UserAlreadyExistsException();
        }

        if (userRepository.existsByPhone(data.getPhone())) {
            throw new UserAlreadyExistsException();
        }

        // Build and hash user credentials
        UserEntity user = UserEntity.builder()
                .email(data.getEmail())
                .phone(data.getPhone())
                .name(data.getName())
                .systemRole(data.getRole())
                .passwordHash(authUtils.hashPassword(data.getPassword()))
                .build();

        UserEntity savedUser = userRepository.save(user);

        // Generate individual token signatures
        String accessToken = authUtils.generateAccessToken(
                user.getId(), user.getEmail(), String.valueOf(user.getSystemRole()));
        String refreshToken = authUtils.generateRefreshToken(
                user.getId(), user.getEmail(), String.valueOf(user.getSystemRole()));

        return AuthResponse.builder()
                .message("Register successful")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(UserResponse.fromEntity(savedUser))
                .build();
    }

    @Transactional(readOnly = true)
    public AuthResponse login(UserLoginDto data) {
        // Find active user record
        UserEntity user = userRepository.findActiveByEmail(data.getEmail())
                .orElseThrow(InvalidCredentialsException::new);

        // Verify hashed password compatibility safely
        if (!authUtils.comparePassword(data.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        // Generate individual token signatures
        String accessToken = authUtils.generateAccessToken(
                user.getId(), user.getEmail(), String.valueOf(user.getSystemRole()));
        String refreshToken = authUtils.generateRefreshToken(
                user.getId(), user.getEmail(), String.valueOf(user.getSystemRole()));

        return AuthResponse.builder()
                .message("Login successful")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(UserResponse.fromEntity(user))
                .build();
    }

    public void forgetPassword(ForgetPasswordDto data) {
        // find user or throw
        Optional<UserEntity> result = userRepository.findActiveByEmail(data.getEmail());

        if (result.isEmpty()) {
            // for security consideration, to prevent brute force attacks to see if email exists
            return;
        }

        UserEntity user = result.get();

        // generate & hash otp
        String otp = authUtils.generateOTP();
        String otpHash = authUtils.hashOTP(otp);

        // insert a new password reset
        PasswordResetsEntity passwordReset = PasswordResetsEntity.builder()
                .user(user)
                .otpHash(otpHash)
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .build();

        passwordResetRepository.save(passwordReset);

        // TODO: send otp to user via mail
        System.out.println("Mock Mail OTP: " + otp);
    }

    public void resetPassword(ResetPasswordDto data) {
        // TODO: complete reset password
    }
}