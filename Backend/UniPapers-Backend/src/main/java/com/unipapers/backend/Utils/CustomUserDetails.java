package com.unipapers.backend.Utils;

import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public record CustomUserDetails(
        Long id,
        String publicId,
        String firstName,
        String lastName,
        String password,
        String email,
        boolean emailVerified,
        long studentNumber,
        int yearOfStudy,
        int semester,
        List<String> roles
) implements UserDetails {

    @Override
    @NullMarked
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (roles == null || roles.isEmpty()) {
            return List.of();
        }
        return roles.stream()
                .filter(Objects::nonNull)
                .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    @NullMarked
    public String getUsername() {
        return Long.toString(studentNumber);
    }

    @Override
    public boolean isEnabled() {
        return emailVerified;
    }
}
