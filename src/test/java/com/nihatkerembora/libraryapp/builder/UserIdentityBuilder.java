package com.nihatkerembora.libraryapp.builder;

import com.nihatkerembora.libraryapp.auth.model.enums.TokenClaims;
import com.nihatkerembora.libraryapp.auth.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserIdentityBuilder {

    private final TokenService tokenService;

    public String extractUserIdFromToken(String accessToken) {
        return tokenService.getPayload(accessToken)
                .get(TokenClaims.USER_ID.getValue(), String.class);
    }

}
