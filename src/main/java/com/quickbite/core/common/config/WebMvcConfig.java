package com.quickbite.core.common.config;

import com.quickbite.core.common.interceptor.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final LoggingInterceptor loggingInterceptor;
    private final RBACPermissionInterceptor rbacPermissionInterceptor;
    private final RequireRestaurantMemberInterceptor requireRestaurantMemberInterceptor;
    private final RequireBranchAccessInterceptor requireBranchAccessInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Register interceptor for all API endpoints
        registry.addInterceptor(loggingInterceptor).addPathPatterns("/**");
        registry.addInterceptor(rbacPermissionInterceptor).addPathPatterns("/**");
        registry.addInterceptor(requireRestaurantMemberInterceptor).addPathPatterns("/**");
        registry.addInterceptor(requireBranchAccessInterceptor).addPathPatterns("/**");
    }
}