package com.quickbite.core.rbac.repository;

import com.quickbite.core.rbac.domain.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity, Short> {
    Optional<RoleEntity> findFirstByName(String roleName);
}
