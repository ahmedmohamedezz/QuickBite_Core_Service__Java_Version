package com.quickbite.core.auth.repository;

import com.quickbite.core.auth.domain.PasswordResetsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetRepository extends JpaRepository<PasswordResetsEntity, Long> {
}
