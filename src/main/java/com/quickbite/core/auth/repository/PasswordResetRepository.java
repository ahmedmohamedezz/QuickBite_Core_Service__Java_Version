package com.quickbite.core.auth.repository;

import com.quickbite.core.auth.domain.PasswordResetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PasswordResetRepository extends JpaRepository<PasswordResetEntity, Long> {
    Optional<PasswordResetEntity> findFirstByUserIdAndConsumedAtIsNullOrderByCreatedAtDesc(Long userId);

    @Modifying
    @Query("UPDATE PasswordResetEntity pr SET pr.consumedAt = CURRENT_TIMESTAMP WHERE pr.id = :id")
    void updateConsumedAt(Long id);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO password_resets (user_id, otp_hash, expires_at) " +
            "VALUES (:userId, :otpHash, :expiresAt)"
            , nativeQuery = true)
    void createOTP(Long userId, String otpHash, LocalDateTime expiresAt);
}
