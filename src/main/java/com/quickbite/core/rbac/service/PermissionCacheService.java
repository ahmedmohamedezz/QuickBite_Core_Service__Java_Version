package com.quickbite.core.rbac.service;

import com.quickbite.core.common.config.AppConfig;
import com.quickbite.core.rbac.dto.PermissionCacheInfo;
import com.quickbite.core.rbac.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PermissionCacheService {
    private final PermissionRepository permissionRepository;

    private final Long TTL;

    // roleName -> {String[]: permissions, cachedAt: Long}
    private final Map<String, PermissionCacheInfo> cache;

    public PermissionCacheService(AppConfig appConfig, PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
        this.cache = new HashMap<>();
        this.TTL = appConfig.permissions().TTL();
    }

    public List<String> getPermissions(String roleName) {
        PermissionCacheInfo cached = cache.getOrDefault(roleName, null);

        boolean expired = (
                cached != null && cached.cachedAt() != null &&
                        cached.cachedAt().plus(TTL, ChronoUnit.MILLIS).isBefore(LocalDateTime.now())
        );

        if (cached == null || expired) {
            List<String> permissions = permissionRepository.getPermissionDetailsByRoleName(roleName);
            cached = new PermissionCacheInfo(permissions, LocalDateTime.now());
            cache.put(roleName, cached);
        }

        return cache.get(roleName).permissions();
    }

    public boolean hasPermission(List<String> permissions, String resource, String action) {
        return permissions.contains(formatPermission(resource, action));
    }

    public String formatPermission(String resource, String action) {
        return resource + ":" + action;
    }
}
