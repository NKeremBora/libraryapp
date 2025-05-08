package com.nihatkerembora.libraryapp.auth.model.mapper;

import com.nihatkerembora.libraryapp.auth.model.Token;
import com.nihatkerembora.libraryapp.auth.model.dto.response.TokenResponse;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility sınıfı: Token → TokenResponse dönüşümü.
 */
public final class TokenMapper {

    private TokenMapper() {
        // Prevent instantiation
    }

    /**
     * Tek bir Token → TokenResponse DTO dönüşümü.
     *
     * @param token domain model
     * @return TokenResponse DTO, token null ise null döner
     */
    public static TokenResponse toDto(Token token) {
        if (token == null) {
            return null;
        }
        return TokenResponse.builder()
                .accessToken(token.getAccessToken())
                .refreshToken(token.getRefreshToken())
                .accessTokenExpiresAt(token.getAccessTokenExpiresAt())
                .build();
    }

    /**
     * Liste halinde Token → List<TokenResponse>
     *
     * @param tokens domain model
     * @return DTO
     */
    public static List<TokenResponse> toDtoList(List<Token> tokens) {
        if (tokens == null || tokens.isEmpty()) {
            return List.of();
        }
        return tokens.stream()
                .map(TokenMapper::toDto)
                .collect(Collectors.toList());
    }
}
