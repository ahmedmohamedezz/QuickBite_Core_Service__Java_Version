package com.quickbite.core.auth.repository;

import com.quickbite.core.auth.domain.PasswordResetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface PasswordResetRepository extends JpaRepository<PasswordResetEntity, Long> {
    Optional<PasswordResetEntity> findFirstByUserIdAndConsumedAtIsNullOrderByCreatedAtDesc(Long userId);

    @Modifying
    @Query("UPDATE PasswordResetEntity pr SET pr.consumedAt = CURRENT_TIMESTAMP WHERE pr.id = :id")
    void updateConsumedAt(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO password_resets (user_id, otp_hash, expires_at) " +
            "VALUES (:userId, :otpHash, NOW() + INTERVAL '10 minutes')"
            , nativeQuery = true)
    void createOTP(@Param("userId")  Long userId, @Param("otpHash") String otpHash);
}
