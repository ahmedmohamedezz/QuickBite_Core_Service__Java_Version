package com.quickbite.core.common.interceptor;

import com.quickbite.core.common.annotation.RequireBranchAccess;
import com.quickbite.core.common.annotation.RequireRestaurantMember;
import com.quickbite.core.common.exception.impl.NotAuthenticatedException;
import com.quickbite.core.common.exception.impl.PermissionDeniedException;
import com.quickbite.core.common.exception.impl.ServerException;
import com.quickbite.core.common.security.UserPrincipal;
import com.quickbite.core.user.enums.SystemRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class RequireBranchAccessInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Skip non-controller requests (e.g., static resources)
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        // Check if the route requires dynamic permission verification
        RequireBranchAccess annotation = handlerMethod.getMethodAnnotation(RequireBranchAccess.class);
        if (annotation == null) {
            return true; // No dynamic permission check required for this endpoint
        }

        UserPrincipal user = getCurrentUser();
        String paramName = annotation.pathVariable();

        if (paramName == null) {
            throw new ServerException();
        }

        @SuppressWarnings("unchecked")
        Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        String paramValue = pathVariables.getOrDefault(paramName, null);

        if (paramValue == null) {
            throw new ServerException();
        }

        Long branchId = Long.valueOf(paramValue);

        // if system_admin, bypass
        if (user.getSystemRole().equals(SystemRole.system_admin)) {
            return true;
        }

        // if restaurant_user, validate
        if (user.getSystemRole().equals(SystemRole.restaurant_user)) {
            if (!user.getBranchIds().contains(branchId)) {
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
