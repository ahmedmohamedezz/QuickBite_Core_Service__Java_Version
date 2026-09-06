package com.quickbite.core.auth.service;

import com.quickbite.core.auth.dto.*;
import com.quickbite.core.auth.repository.PasswordResetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final PasswordResetRepository passwordResetRepository;


    @Transactional
    public void createPasswordResetRecord(Long userId, String otpHash, LocalDateTime expiresAt) {
        passwordResetRepository.createOTP(userId, otpHash, expiresAt);
    }

}