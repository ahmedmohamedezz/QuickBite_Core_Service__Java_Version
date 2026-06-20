package com.quickbite.core.common.filter;

import com.quickbite.core.auth.dto.JwtPayload;
import com.quickbite.core.auth.utils.AuthUtils;
import com.quickbite.core.common.config.AppConfig;
import com.quickbite.core.common.security.UserPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthUtils authUtils;
    private final AppConfig appConfig;
    private final UserDetailsService userDetailsService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public JwtAuthenticationFilter(AuthUtils authUtils, AppConfig appConfig, UserDetailsService userDetailsService) {
        this.authUtils = authUtils;
        this.appConfig = appConfig;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // get token from cookies
        Cookie[] cookies = request.getCookies();
        String accessToken = (cookies != null) ? Arrays.stream(cookies)
                .filter(c -> appConfig.cookies().accessTokenName().equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null) : null;

        try {
            logger.info("Auth header found, validating request.");

            JwtPayload payload = authUtils.verifyAccessToken(accessToken);

            if (payload != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                String role = payload.role();
                String email = payload.email();

                logger.info("Got user {}, with email {}", payload.userId(), payload.email());

                // Build Spring's internal authentication identity representation
                UserPrincipal user = (UserPrincipal) userDetailsService.loadUserByUsername(email);
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        user, // Principal (can be accessed via authentication.getPrincipal())
                        null,
                        List.of(authority)
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Commit this user identity context to the request execution thread
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            logger.error("Failed to set user authentication in security context", e);
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return path.startsWith("/auth") || path.startsWith("/health");
    }
}