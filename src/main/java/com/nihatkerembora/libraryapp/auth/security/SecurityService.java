package com.nihatkerembora.libraryapp.auth.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component("securityService")
public class SecurityService {

    public boolean canAccessUser(Authentication authentication, String userId) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMIN"));

        if (isAdmin) {
            return true;
        }

        Jwt jwt = (Jwt) authentication.getPrincipal();
        String currentUserId = jwt.getClaim("userId");

        return currentUserId.equals(userId);
    }
}
