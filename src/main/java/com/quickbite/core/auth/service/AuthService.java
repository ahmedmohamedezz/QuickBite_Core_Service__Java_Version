package com.quickbite.core.auth.service;

import com.quickbite.core.auth.utils.AuthUtils;
import com.quickbite.core.auth.domain.PasswordResetEntity;
import com.quickbite.core.auth.dto.*;
import com.quickbite.core.auth.exception.CannotSignupAsSystemAdminException;
import com.quickbite.core.auth.exception.InvalidOTPException;
import com.quickbite.core.auth.exception.UserAlreadyExistsException;
import com.quickbite.core.auth.exception.InvalidCredentialsException;
import com.quickbite.core.auth.repository.PasswordResetRepository;
import com.quickbite.core.restaurant.dto.RestaurantDto;
import com.quickbite.core.restaurant.exception.RestaurantDataRequiredException;
import com.quickbite.core.restaurant.service.RestaurantService;
import com.quickbite.core.user.domain.UserEntity;
import com.quickbite.core.user.enums.SystemRole;
import com.quickbite.core.user.mapper.UserMapper;
import com.quickbite.core.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordResetRepository passwordResetRepository;
    private final UserMapper userMapper;
    private final AuthUtils authUtils;
    private final RestaurantService restaurantService;

    @Transactional
    public AuthResponse register(UserRegisterDto data) {
        // system admins are configured manually
        if (data.role() == SystemRole.system_admin) {
            throw new CannotSignupAsSystemAdminException();
        }

        if (userRepository.existsByEmail(data.email())) {
            throw new UserAlreadyExistsException();
        }

        if (userRepository.existsByPhone(data.phone())) {
            throw new UserAlreadyExistsException();
        }

        // Build and hash user credentials
        UserEntity user = UserEntity.builder()
                .email(data.email())
                .phone(data.phone())
                .name(data.name())
                .systemRole(data.role())
                .passwordHash(authUtils.hashPassword(data.password()))
                .build();

        UserEntity savedUser = userRepository.save(user);
        RestaurantDto restaurantDto = null;
        // create restaurant_id if present
        if (data.role().equals(SystemRole.restaurant_user)) {

            if (data.restaurant() == null) {
                throw new RestaurantDataRequiredException();
            }

            restaurantDto = restaurantService.create(savedUser.getId(), data.restaurant());
        }


        // Generate individual token signatures
        String accessToken = authUtils.generateAccessToken(
                user.getId(), user.getEmail(), String.valueOf(user.getSystemRole()));
        String refreshToken = authUtils.generateRefreshToken(
                user.getId(), user.getEmail(), String.valueOf(user.getSystemRole()));

        return AuthResponse.builder()
                .message("Register successful")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(userMapper.toDto(savedUser))
                .restaurant(restaurantDto)
                .build();
    }

    @Transactional(readOnly = true)
    public AuthResponse login(UserLoginDto data) {
        // Find active user record
        UserEntity user = userRepository.findActiveByEmail(data.email())
                .orElseThrow(InvalidCredentialsException::new);

        // Verify hashed password compatibility safely
        if (!authUtils.comparePassword(data.password(), user.getPasswordHash())) {
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
                .user(userMapper.toDto(user))
                .build();
    }

    @Transactional
    public void forgetPassword(ForgetPasswordDto data) {
        Optional<UserEntity> result = userRepository.findActiveByEmail(data.email());

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
        UserEntity user = userRepository.findActiveByEmail(data.email())
                .orElseThrow(InvalidOTPException::new);

        PasswordResetEntity passwordReset =
                passwordResetRepository
                        .findFirstByUserIdAndConsumedAtIsNullOrderByCreatedAtDesc(user.getId())
                        .orElseThrow(InvalidOTPException::new);

        String otpHash = authUtils.hashOTP(data.otp());

        if (!passwordReset.getOtpHash().equals(otpHash) || passwordReset.isExpired()) {
            throw new InvalidOTPException();
        }

        userRepository.updatePassword(user.getId(), authUtils.hashPassword(data.newPassword()));
        passwordResetRepository.updateConsumedAt(passwordReset.getId());
    }

    public String refreshToken(String refreshToken) {
        JwtPayload payload = authUtils.verifyRefreshToken(refreshToken);
        return authUtils.generateAccessToken(payload.userId(), payload.email(), payload.role());
    }
}