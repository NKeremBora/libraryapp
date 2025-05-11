package com.nihatkerembora.libraryapp.auth.service;

import com.nihatkerembora.libraryapp.auth.exception.PasswordNotValidException;
import com.nihatkerembora.libraryapp.auth.exception.UserNotFoundException;
import com.nihatkerembora.libraryapp.auth.model.Token;
import com.nihatkerembora.libraryapp.auth.model.dto.request.LoginRequest;
import com.nihatkerembora.libraryapp.auth.model.entity.UserEntity;
import com.nihatkerembora.libraryapp.auth.repository.UserRepository;
import com.nihatkerembora.libraryapp.auth.service.impl.AuthServiceImpl;
import com.nihatkerembora.libraryapp.base.AbstractBaseServiceTest;
import com.nihatkerembora.libraryapp.builder.UserEntityBuilder;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class AuthServiceImplLoginTest extends AbstractBaseServiceTest {

    @InjectMocks
    private AuthServiceImpl loginService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @Test
    void login_ValidCredentials_ReturnsToken() {

        // Given
        final LoginRequest loginRequest = LoginRequest.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        final UserEntity adminEntity = new UserEntityBuilder().withValidFields().build();

        final Token expectedToken = Token.builder()
                .accessToken("mockAccessToken")
                .accessTokenExpiresAt(123456789L)
                .refreshToken("mockRefreshToken")
                .build();

        // When
        when(userRepository.findByEmail(loginRequest.getEmail()))
                .thenReturn(Optional.of(adminEntity));

        when(passwordEncoder.matches(loginRequest.getPassword(), adminEntity.getPassword()))
                .thenReturn(true);

        when(tokenService.generateToken(adminEntity.getClaims())).thenReturn(expectedToken);

        Token actualToken = loginService.login(loginRequest);

        // Then
        assertEquals(expectedToken.getAccessToken(), actualToken.getAccessToken());
        assertEquals(expectedToken.getRefreshToken(), actualToken.getRefreshToken());
        assertEquals(expectedToken.getAccessTokenExpiresAt(), actualToken.getAccessTokenExpiresAt());

        // Verify
        verify(userRepository).findByEmail(loginRequest.getEmail());
        verify(passwordEncoder).matches(loginRequest.getPassword(), adminEntity.getPassword());
        verify(tokenService).generateToken(adminEntity.getClaims());

    }

    @Test
    void login_InvalidEmail_ThrowsAdminNotFoundException() {

        // Given
        final LoginRequest loginRequest = LoginRequest.builder()
                .email("nonexistent@example.com")
                .password("password123")
                .build();

        // When
        when(userRepository.findByEmail(loginRequest.getEmail()))
                .thenReturn(Optional.empty());

        // Then
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> loginService.login(loginRequest));

        assertEquals("User not found! " + loginRequest.getEmail(), exception.getMessage());

        // Verify
        verify(userRepository).findByEmail(loginRequest.getEmail());
        verifyNoInteractions(passwordEncoder, tokenService);

    }

    @Test
    void login_InvalidPassword_ThrowsPasswordNotValidException() {

        // Given
        final LoginRequest loginRequest = LoginRequest.builder()
                .email("test@example.com")
                .password("invalidPassword")
                .build();

        final UserEntity adminEntity = UserEntity.builder()
                .email(loginRequest.getEmail())
                .password("encodedPassword")
                .build();

        // When
        when(userRepository.findByEmail(loginRequest.getEmail()))
                .thenReturn(Optional.of(adminEntity));

        when(passwordEncoder.matches(loginRequest.getPassword(), adminEntity.getPassword()))
                .thenReturn(false);

        // Then
        PasswordNotValidException exception = assertThrows(PasswordNotValidException.class,
                () -> loginService.login(loginRequest));

        assertNotNull(exception);

        // Verify
        verify(userRepository).findByEmail(loginRequest.getEmail());
        verify(passwordEncoder).matches(loginRequest.getPassword(), adminEntity.getPassword());
        verifyNoInteractions(tokenService);

    }

}
