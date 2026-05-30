package com.quickbite.core.common.filter;

import com.quickbite.core.common.security.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // skip request
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.info("Skipping jwt filter, auth header not found");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            logger.info("Auth header found, validating request");
            String token = authHeader.substring(7);
            Claims claims = jwtService.validateAndExtractAccessClaims(token);

            if (claims != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                String userId = claims.getSubject();
                String role = claims.get("role", String.class);

                // Build Spring's internal authentication identity representation
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userId, // Principal (can be accessed via authentication.getPrincipal())
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
}