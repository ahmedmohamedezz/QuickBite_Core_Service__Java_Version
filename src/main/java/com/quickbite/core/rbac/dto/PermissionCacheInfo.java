package com.quickbite.core.rbac.dto;

import java.time.LocalDateTime;
import java.util.List;

public record PermissionCacheInfo(
        List<String> permissions,
        LocalDateTime cachedAt
) {
}
