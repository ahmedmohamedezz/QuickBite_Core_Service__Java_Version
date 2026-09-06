package com.quickbite.core.common.security;

import com.quickbite.core.auth.dto.JwtPayload;
import com.quickbite.core.user.domain.UserEntity;
import com.quickbite.core.user.enums.SystemRole;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class UserPrincipal implements UserDetails {

    private final Long id;
    private final String email;
    private final SystemRole systemRole;
//    private final String passwordHash;
    private final Collection<? extends GrantedAuthority> authorities;
    // for restaurant users only
    private final Long restaurantId;        // restaurant user work at
    private final String restaurantRole;    // role in that restaurant
    private final List<Long> branchIds;     // what branches user can access

//    public UserPrincipal(UserEntity user) {
//        this.id = user.getId();
//        this.email = user.getEmail();
////        this.passwordHash = user.getPasswordHash();
//        // Spring Security roles conventionally look like "ROLE_CUSTOMER" or "ROLE_ADMIN"
//        this.authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getSystemRole().name()));
//        this.restaurantId = null;
//        this.restaurantRole = null;
//        this.branchIds = null;
//    }

    public UserPrincipal(JwtPayload jwtPayload) {
        this.id = jwtPayload.userId();
        this.email = jwtPayload.email();
        this.systemRole = SystemRole.valueOf(jwtPayload.role().toLowerCase());
//        this.passwordHash = user.getPasswordHash();
        // Spring Security roles conventionally look like "ROLE_CUSTOMER" or "ROLE_ADMIN"
        this.authorities = List.of(new SimpleGrantedAuthority("ROLE_" + jwtPayload.role()));
        this.restaurantId = jwtPayload.restaurantId();
        this.restaurantRole = jwtPayload.restaurantRole();
        this.branchIds = jwtPayload.branchIds();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return email;
    } // Email is our primary credential identifier

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}