package com.quickbite.core.common.security;

import com.quickbite.core.auth.dto.JwtPayload;
import com.quickbite.core.common.config.AppConfig;
import com.quickbite.core.rbac.dto.MemberWithRoleNameProjection;
import com.quickbite.core.rbac.service.MemberService;
import com.quickbite.core.user.dto.UserDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class JwtService {

    private final Logger logger = LoggerFactory.getLogger(JwtService.class);
    private final SecretKey accessKey;
    private final SecretKey refreshKey;
    private final long accessExpirationMs;
    private final long refreshExpirationMs;
    private final MemberService memberService;

    public JwtService(AppConfig appConfig, MemberService memberService) {
        this.memberService = memberService;
        AppConfig.JwtConfig jwtConfig = appConfig.jwt();

        this.accessKey = Keys.hmacShaKeyFor(jwtConfig.accessSecret().getBytes(StandardCharsets.UTF_8));
        this.refreshKey = Keys.hmacShaKeyFor(jwtConfig.refreshSecret().getBytes(StandardCharsets.UTF_8));

        this.accessExpirationMs = jwtConfig.accessExpiresInMs();
        this.refreshExpirationMs = jwtConfig.refreshExpiresInMs();
    }

    public String generateAccessToken(JwtPayload payload) {
        return buildToken(payload, accessExpirationMs, accessKey);
    }

    public String generateRefreshToken(JwtPayload payload) {
        return buildToken(payload, refreshExpirationMs, refreshKey);
    }

    public JwtPayload verifyAccessToken(String token) {
        return parseClaims(validateAndExtract(token, accessKey));
    }

    public JwtPayload verifyRefreshToken(String token) {
        return parseClaims(validateAndExtract(token, refreshKey));
    }

    private String buildToken(JwtPayload payload, long expirationMs, SecretKey key) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(String.valueOf(payload.userId()))
                .claims(Map.of(
                        "email", payload.email(),
                        "role", payload.role(),
                        "restaurantId", payload.restaurantId(),
                        "restaurantRole", payload.restaurantRole(),
                        "branchIds", payload.branchIds()
                ))
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    private Claims validateAndExtract(String token, SecretKey key) {
        Claims claims = null;
        try {
            claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (Exception e) {
            // This is likely what you have right now, but missing the ', e' at the end to print the trace
            logger.error("Failed to set user authentication in security context", e);
        }

        return claims;
    }


    private JwtPayload parseClaims(Claims claims) {
        List<?> rawBranchIds = claims.get("branchIds", List.class);
        List<Long> branchIds = (rawBranchIds == null) ? List.of() : rawBranchIds.stream()
                .map(id -> ((Number) id).longValue())
                .toList();

        return new JwtPayload(
                Long.parseLong(claims.getSubject()),
                claims.get("email", String.class),
                claims.get("role", String.class),
                claims.get("restaurantId", Long.class),
                claims.get("restaurantRole", String.class),
                branchIds
        );
    }

    @Transactional(readOnly = true)
    public JwtPayload buildJwtPayload(UserDto user) {
        Optional<MemberWithRoleNameProjection> optionalMemberInfo =
                memberService.findMemberWithRoleName(user.id());

        if (optionalMemberInfo.isEmpty()) {
            return new JwtPayload(
                    user.id(),
                    user.email(),
                    String.valueOf(user.systemRole()),
                    null, null, null
            );
        }

        MemberWithRoleNameProjection memberInfo = optionalMemberInfo.get();
        List<Long> branchIds = memberService.findBranchIdsByMemberId(memberInfo.getMemberId());

        return new JwtPayload(
                user.id(),
                user.email(),
                String.valueOf(user.systemRole()),
                memberInfo.getRestaurantId(),
                memberInfo.getRoleName(),
                branchIds
        );
    }
}