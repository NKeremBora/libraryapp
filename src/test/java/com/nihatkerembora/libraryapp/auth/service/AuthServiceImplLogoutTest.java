package com.nihatkerembora.libraryapp.auth.service;


import com.nihatkerembora.libraryapp.auth.model.dto.request.TokenInvalidateRequest;
import com.nihatkerembora.libraryapp.auth.model.entity.UserEntity;
import com.nihatkerembora.libraryapp.auth.service.impl.AuthServiceImpl;
import com.nihatkerembora.libraryapp.base.AbstractBaseServiceTest;
import com.nihatkerembora.libraryapp.builder.AdminEntityBuilder;
import com.nihatkerembora.libraryapp.builder.TokenBuilder;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Set;

import static org.mockito.Mockito.*;

class AuthServiceImplLogoutTest extends AbstractBaseServiceTest {

    @InjectMocks
    private AuthServiceImpl logoutService;

    @Mock
    private TokenService tokenService;

    @Mock
    private InvalidTokenService invalidTokenService;

    @Test
    void givenAccessTokenAndRefreshToken_whenLogoutForAdmin_thenReturnLogout() {

        // Given
        final UserEntity mockUserEntity = new AdminEntityBuilder().withValidFields().build();

        final Claims mockAccessTokenClaims = TokenBuilder.getValidClaims(
                mockUserEntity.getId(),
                mockUserEntity.getFirstName()
        );

        final Claims mockRefreshTokenClaims = TokenBuilder.getValidClaims(
                mockUserEntity.getId(),
                mockUserEntity.getFirstName()
        );

        final String mockAccessTokenId = mockAccessTokenClaims.getId();
        final String mockRefreshTokenId = mockRefreshTokenClaims.getId();


        final String accessToken = "validAccessToken";
        final String refreshToken = "validRefreshToken";

        final TokenInvalidateRequest tokenInvalidateRequest = TokenInvalidateRequest.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        // When
        doNothing().when(tokenService).verifyAndValidate(Set.of(accessToken, refreshToken));
        when(tokenService.getPayload(accessToken)).thenReturn(mockAccessTokenClaims);
        doNothing().when(invalidTokenService).checkForInvalidityOfToken(mockAccessTokenId);
        when(tokenService.getPayload(refreshToken)).thenReturn(mockRefreshTokenClaims);
        doNothing().when(invalidTokenService).checkForInvalidityOfToken(mockRefreshTokenId);
        doNothing().when(invalidTokenService).invalidateTokens(Set.of(mockAccessTokenId, mockRefreshTokenId));

        // Then
        logoutService.logout(tokenInvalidateRequest);

        // Verify
        verify(tokenService).verifyAndValidate(Set.of(accessToken, refreshToken));
        verify(tokenService, times(2)).getPayload(anyString());
        verify(invalidTokenService, times(2)).checkForInvalidityOfToken(anyString());
        verify(invalidTokenService).invalidateTokens(anySet());

    }

}