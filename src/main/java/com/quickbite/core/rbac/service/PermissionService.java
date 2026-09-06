package com.quickbite.core.rbac.service;

import com.quickbite.core.rbac.dto.RolePermissionProjection;
import com.quickbite.core.rbac.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PermissionService {
    private final PermissionRepository permissionRepository;

    public Map<String, Object> getPermissionDetailsByRoleName(String roleName) {
        List<String> details = permissionRepository.getPermissionDetailsByRoleName(roleName);

        return Map.of("role", roleName, "permissions", details);
    }
}
