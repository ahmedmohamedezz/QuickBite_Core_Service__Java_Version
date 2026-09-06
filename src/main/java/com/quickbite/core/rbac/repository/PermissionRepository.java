package com.quickbite.core.rbac.repository;

import com.quickbite.core.rbac.domain.PermissionEntity;
import com.quickbite.core.rbac.dto.RolePermissionProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<PermissionEntity, Long> {
    @Query(value = """
                    SELECT p.resource, p.action
                    FROM permissions p
                    JOIN role_permissions rp ON rp.permission_id = p.id
                    JOIN roles r ON rp.role_id = r.id
                    WHERE r.name = :roleName
            """, nativeQuery = true)
    List<RolePermissionProjection> getPermissionsByRoleName(String roleName);

    default List<String> getPermissionDetailsByRoleName(String roleName) {
        return this.getPermissionsByRoleName(roleName).stream().map(row -> row.getResource() +
                ":" + row.getAction()).toList();
    }

    @Query(value = """
                    SELECT p.resource, p.action
                    FROM permissions p
                    JOIN role_permissions rp ON rp.permission_id = p.id
                    JOIN roles r ON rp.role_id = r.id
                    WHERE r.name = :roleName
            """, nativeQuery = true)
    public List<RolePermissionProjection> getRolePermissionsAndDescription(String roleName);
}
