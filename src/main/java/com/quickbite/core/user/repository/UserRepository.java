package com.quickbite.core.user.repository;

import com.quickbite.core.user.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByPhone(String phone);

    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);

    @Query("SELECT u FROM UserEntity u WHERE u.email = :email AND u.deletedAt IS null")
    Optional<UserEntity> findActiveByEmail(@Param("email") String email);
}
