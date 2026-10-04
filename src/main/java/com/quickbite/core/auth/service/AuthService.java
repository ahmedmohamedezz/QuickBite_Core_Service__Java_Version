package com.quickbite.core.auth.service;

import com.quickbite.core.auth.utils.AuthUtils;
import com.quickbite.core.auth.domain.PasswordResetEntity;
import com.quickbite.core.auth.dto.*;
import com.quickbite.core.auth.exception.CannotSignupAsSystemAdminException;
import com.quickbite.core.auth.exception.InvalidOTPException;
import com.quickbite.core.auth.exception.InvalidCredentialsException;
import com.quickbite.core.auth.repository.PasswordResetRepository;
import com.quickbite.core.pkg.mail.EmailService;
import com.quickbite.core.common.security.JwtService;
import com.quickbite.core.rbac.service.MemberService;
import com.quickbite.core.restaurant.dto.RestaurantDto;
import com.quickbite.core.restaurant.exception.RestaurantDataRequiredException;
import com.quickbite.core.restaurant.service.RestaurantService;
import com.quickbite.core.user.dto.UserDto;
import com.quickbite.core.user.enums.SystemRole;
import com.quickbite.core.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final PasswordResetRepository passwordResetRepository;
    private final AuthUtils authUtils;
    private final JwtService jwtService;
    private final RestaurantService restaurantService;
    private final EmailService emailService;
    private final MemberService memberService;

    @Transactional
    public AuthResponse register(UserRegisterDto data) {
        // system admins are configured manually
        if (data.role() == SystemRole.system_admin) {
            throw new CannotSignupAsSystemAdminException();
        }

        UserDto user = userService.create(data);

        RestaurantDto restaurantDto = null;
        // create restaurant_id if present
        if (data.role().equals(SystemRole.restaurant_user)) {

            if (data.restaurant() == null) {
                throw new RestaurantDataRequiredException();
            }

            restaurantDto = restaurantService.create(user.id(), data.restaurant());
            memberService.createOwnerMember(restaurantDto.id(), user.id());
        }

        // Generate individual token signatures
        JwtPayload payload = jwtService.buildJwtPayload(user);
        String accessToken = jwtService.generateAccessToken(payload);
        String refreshToken = jwtService.generateRefreshToken(payload);

        return AuthResponse.builder()
                .message("Register successful")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(user)
                .restaurant(restaurantDto)
                .build();
    }

    @Transactional(readOnly = true)
    public AuthResponse login(UserLoginDto data) {
        // Find active user record
        UserDto user = userService.findByEmail(data.email());

        String hashed = authUtils.hashPassword(data.password());
        String dbPass = user.passwordHash();

        // Verify hashed password compatibility safely
        if (!authUtils.comparePassword(data.password(), user.passwordHash())) {
            throw new InvalidCredentialsException();
        }

        // Generate individual token signatures
        JwtPayload payload = jwtService.buildJwtPayload(user);

        String accessToken = jwtService.generateAccessToken(payload);
        String refreshToken = jwtService.generateRefreshToken(payload);

        return AuthResponse.builder()
                .message("Login successful")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(user)
                .build();
    }

    @Transactional
    public void forgetPassword(ForgetPasswordDto data) {
        UserDto user = userService.findByEmail(data.email());

        // generate & hash otp
        String otp = authUtils.generateOTP();
        String otpHash = authUtils.hashOTP(otp);

        passwordResetRepository.createOTP(user.id(), otpHash, LocalDateTime.now().plusMinutes(10));

        emailService.mockEmail("Mock Mail OTP: ", otp);
    }

    @Transactional
    public UserDto resetPassword(ResetPasswordDto data) {
        UserDto user = userService.findByEmail(data.email());

        PasswordResetEntity passwordReset = getLastPasswordResetByUserId(user.id());

        if (!authUtils.validateOTP(data.otp(), passwordReset.getOtpHash()) || passwordReset.isExpired()) {
            throw new InvalidOTPException();
        }

        userService.updatePassword(user.id(), authUtils.hashPassword(data.newPassword()));
        passwordResetRepository.updateConsumedAt(passwordReset.getId());

        return user;
    }

    private PasswordResetEntity getLastPasswordResetByUserId(Long userId) {
        return passwordResetRepository
                .findFirstByUserIdAndConsumedAtIsNullOrderByCreatedAtDesc(userId)
                .orElseThrow(InvalidOTPException::new);
    }

    public String refreshToken(String refreshToken) {
        JwtPayload payload = jwtService.verifyRefreshToken(refreshToken);
        return jwtService.generateAccessToken(payload);
    }

    @Transactional
    public AuthResponse acceptInvite(ResetPasswordDto data) {
        UserDto user = this.resetPassword(data);

        memberService.activateMemberByUserId(user.id());

        JwtPayload payload = jwtService.buildJwtPayload(user);

        // return updated user + tokens
        String accessToken = jwtService.generateAccessToken(payload);
        String refreshToken = jwtService.generateRefreshToken(payload);

        return new AuthResponse(
                "Invitation accepted successfully",
                accessToken,
                refreshToken,
                user
        );
    }


}