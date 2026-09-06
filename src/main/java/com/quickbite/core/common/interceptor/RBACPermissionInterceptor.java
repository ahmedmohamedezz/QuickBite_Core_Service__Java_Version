package com.quickbite.core.common.interceptor;

import com.quickbite.core.common.annotation.CheckPermission;
import com.quickbite.core.common.exception.impl.NotAuthenticatedException;
import com.quickbite.core.common.exception.impl.PermissionDeniedException;
import com.quickbite.core.common.security.UserPrincipal;
import com.quickbite.core.rbac.service.PermissionCacheService;
import com.quickbite.core.user.enums.SystemRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RBACPermissionInterceptor implements HandlerInterceptor {
    private final PermissionCacheService permissionCacheService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Skip non-controller requests (e.g., static resources)
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        // Check if the route requires dynamic permission verification
        CheckPermission annotation = handlerMethod.getMethodAnnotation(CheckPermission.class);
        if (annotation == null) {
            return true; // No dynamic permission check required for this endpoint
        }

        UserPrincipal user = getCurrentUser();
        String resource = annotation.resource();
        String action = annotation.action();

        // if system_admin, bypass
        if (user.getSystemRole().equals(SystemRole.system_admin)) {
            return true;
        }

        // if restaurant_user, validate
        if (user.getSystemRole().equals(SystemRole.restaurant_user)) {
            List<String> permissions =
                    permissionCacheService.getPermissions(user.getRestaurantRole());

            if (!permissionCacheService.hasPermission(permissions, resource, action)) {
                throw new PermissionDeniedException();
            }

            return true;
        }

        // otherwise reject
        return false;
    }

    private UserPrincipal getCurrentUser() {
        UserPrincipal user =
                (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (user == null) {
            throw new NotAuthenticatedException();
        }

        return user;
    }
}
