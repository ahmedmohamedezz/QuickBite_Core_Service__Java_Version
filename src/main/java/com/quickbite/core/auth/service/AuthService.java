package com.quickbite.core.auth.service;

import com.quickbite.core.auth.utils.AuthUtils;
import com.quickbite.core.auth.domain.PasswordResetEntity;
import com.quickbite.core.auth.dto.*;
import com.quickbite.core.auth.exception.CannotSignupAsSystemAdminException;
import com.quickbite.core.auth.exception.InvalidOTPException;
import com.quickbite.core.auth.exception.UserAlreadyExistsException;
import com.quickbite.core.auth.exception.InvalidCredentialsException;
import com.quickbite.core.auth.repository.PasswordResetRepository;
import com.quickbite.core.user.domain.UserEntity;
import com.quickbite.core.user.dto.UserResponse;
import com.quickbite.core.user.enums.SystemRole;
import com.quickbite.core.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public void forgetPassword(ForgetPasswordDto data) {
        // find user or throw
        Optional<UserEntity> result = userRepository.findActiveByEmail(data.getEmail());

        if (result.isEmpty()) {
            // for security consideration, to prevent enumeration attacks
            return;
        }

        UserEntity user = result.get();

        // generate & hash otp
        String otp = authUtils.generateOTP();
        String otpHash = authUtils.hashOTP(otp);

        passwordResetRepository.createOTP(user.getId(), otpHash);

        // TODO: send otp to user via mail
        System.out.println("Mock Mail OTP: " + otp);
    }

    @Transactional
    public void resetPassword(ResetPasswordDto data) {
        UserEntity user = userRepository.findActiveByEmail(data.getEmail())
                .orElseThrow(InvalidOTPException::new);

        PasswordResetEntity passwordReset =
                passwordResetRepository
                        .findFirstByUserIdAndConsumedAtIsNullOrderByCreatedAtDesc(user.getId())
                        .orElseThrow(InvalidOTPException::new);

        String otpHash = authUtils.hashOTP(data.getOtp());

        if (!passwordReset.getOtpHash().equals(otpHash) || passwordReset.isExpired()) {
            throw new InvalidOTPException();
        }

        userRepository.updatePassword(user.getId(), authUtils.hashPassword(data.getNewPassword()));
        passwordResetRepository.updateConsumedAt(passwordReset.getId());
    }

    public String refreshToken(String refreshToken) {
        JwtPayload payload = authUtils.verifyRefreshToken(refreshToken);
        return authUtils.generateAccessToken(payload.userId(), payload.email(), payload.role());
    }
}