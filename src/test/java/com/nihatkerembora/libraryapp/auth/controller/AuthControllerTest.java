package com.nihatkerembora.libraryapp.auth.controller;


import com.nihatkerembora.libraryapp.auth.model.Token;
import com.nihatkerembora.libraryapp.auth.model.User;
import com.nihatkerembora.libraryapp.auth.model.dto.request.LoginRequest;
import com.nihatkerembora.libraryapp.auth.model.dto.request.RegisterRequest;
import com.nihatkerembora.libraryapp.auth.model.dto.request.TokenInvalidateRequest;
import com.nihatkerembora.libraryapp.auth.model.dto.request.TokenRefreshRequest;
import com.nihatkerembora.libraryapp.auth.model.dto.response.TokenResponse;
import com.nihatkerembora.libraryapp.auth.model.mapper.TokenMapper;
import com.nihatkerembora.libraryapp.auth.service.AuthService;
import com.nihatkerembora.libraryapp.base.AbstractRestControllerTest;
import com.nihatkerembora.libraryapp.builder.RegisterRequestBuilder;
import com.nihatkerembora.libraryapp.common.model.dto.response.CustomResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthControllerTest extends AbstractRestControllerTest {

    @MockitoBean
    AuthService authService;



    @Test
    void givenValidAdminRegisterRequestWithAdminCreate_whenRegisterAdmin_thenSuccess() throws Exception {

        // Given
        final RegisterRequest request = new RegisterRequestBuilder()
                .withAdminValidFields()
                .build();


        User mockAdminWithCreate = User.builder()
                .id(UUID.randomUUID().toString())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .userType(request.getUserType())
                .build();

        // When
        when(authService.registerUser(any(RegisterRequest.class))).thenReturn(mockAdminWithCreate);

        // Then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(CustomResponse.SUCCESS)));

        // Verify
        verify(authService, times(1)).registerUser(any(RegisterRequest.class));

    }

    @Test
    void givenValidUserRegisterRequestWithUserCreate_whenRegisterUser_thenSuccess() throws Exception {

        // Given
        final RegisterRequest request = new RegisterRequestBuilder()
                .withUserValidFields()
                .build();


        User mockEmployeeWithCreate = User.builder()
                .id(UUID.randomUUID().toString())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .userType(request.getUserType())
                .build();

        // When
        when(authService.registerUser(any(RegisterRequest.class))).thenReturn(mockEmployeeWithCreate);

        // Then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(CustomResponse.SUCCESS)));

        // Verify
        verify(authService, times(1)).registerUser(any(RegisterRequest.class));

    }

    @Test
    void givenLoginRequestWithAdmin_WhenLoginForAdmin_ThenReturnToken() throws Exception {

        // Given
        LoginRequest loginRequest = LoginRequest.builder()
                .email("admin@example.com")
                .password("password")
                .build();

        Token mockToken = Token.builder()
                .accessToken("mockAccessToken")
                .accessTokenExpiresAt(3600L)
                .refreshToken("mockRefreshToken")
                .build();

        TokenResponse expectedTokenResponse = TokenMapper.toDto(mockToken);

        // When
        when(authService.login(any(LoginRequest.class))).thenReturn(mockToken);

        // Then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.httpStatus").value("OK"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isSuccess").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.accessToken").value(expectedTokenResponse.getAccessToken()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.accessTokenExpiresAt").value(expectedTokenResponse.getAccessTokenExpiresAt()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.refreshToken").value(expectedTokenResponse.getRefreshToken()));

        // Verify
        verify(authService, times(1)).login(any(LoginRequest.class));

    }

    @Test
    void givenLoginRequestWithUser_WhenLoginForUser_ThenReturnToken() throws Exception {

        // Given
        LoginRequest loginRequest = LoginRequest.builder()
                .email("employee@example.com")
                .password("password")
                .build();

        Token mockToken = Token.builder()
                .accessToken("mockAccessToken")
                .accessTokenExpiresAt(3600L)
                .refreshToken("mockRefreshToken")
                .build();

        TokenResponse expectedTokenResponse = TokenMapper.toDto(mockToken);

        // When
        when(authService.login(any(LoginRequest.class))).thenReturn(mockToken);

        // Then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.httpStatus").value("OK"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isSuccess").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.accessToken").value(expectedTokenResponse.getAccessToken()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.accessTokenExpiresAt").value(expectedTokenResponse.getAccessTokenExpiresAt()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.refreshToken").value(expectedTokenResponse.getRefreshToken()));

        // Verify
        verify(authService, times(1)).login(any(LoginRequest.class));

    }


    @Test
    void givenTokenRefreshRequestWithAdmin_WhenRefreshTokenForAdmin_ThenReturnTokenResponse() throws Exception {

        // Given
        TokenRefreshRequest tokenRefreshRequest = new TokenRefreshRequest("refreshToken");

        Token mockToken = Token.builder()
                .accessToken("mockAccessToken")
                .accessTokenExpiresAt(3600L)
                .refreshToken("mockRefreshToken")
                .build();

        TokenResponse expectedTokenResponse = TokenMapper.toDto(mockToken);

        // When
        when(authService.refreshToken(any(TokenRefreshRequest.class))).thenReturn(mockToken);

        // Then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tokenRefreshRequest)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.httpStatus").value("OK"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isSuccess").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.accessToken").value(expectedTokenResponse.getAccessToken()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.accessTokenExpiresAt").value(expectedTokenResponse.getAccessTokenExpiresAt()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.refreshToken").value(expectedTokenResponse.getRefreshToken()));

        // Verify
        verify(authService, times(1)).refreshToken(any(TokenRefreshRequest.class));

    }

    @Test
    void givenTokenRefreshRequestWithUser_WhenRefreshTokenForUser_ThenReturnTokenResponse() throws Exception {

        // Given
        TokenRefreshRequest tokenRefreshRequest = new TokenRefreshRequest("refreshToken");

        Token mockToken = Token.builder()
                .accessToken("mockAccessToken")
                .accessTokenExpiresAt(3600L)
                .refreshToken("mockRefreshToken")
                .build();

        TokenResponse expectedTokenResponse = TokenMapper.toDto(mockToken);
        // When
        when(authService.refreshToken(any(TokenRefreshRequest.class))).thenReturn(mockToken);

        // Then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tokenRefreshRequest)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.httpStatus").value("OK"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isSuccess").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.accessToken").value(expectedTokenResponse.getAccessToken()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.accessTokenExpiresAt").value(expectedTokenResponse.getAccessTokenExpiresAt()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.refreshToken").value(expectedTokenResponse.getRefreshToken()));

        // Verify
        verify(authService, times(1)).refreshToken(any(TokenRefreshRequest.class));

    }

    @Test
    void givenTokenInvalidateRequestWithAdmin_WhenLogoutForAdmin_ThenReturnInvalidateToken() throws Exception {

        // Given
        TokenInvalidateRequest tokenInvalidateRequest = TokenInvalidateRequest.builder()
                .accessToken("Bearer " + mockAdminToken.getAccessToken())
                .refreshToken(mockAdminToken.getRefreshToken())
                .build();

        // When
        doNothing().when(authService).logout(any(TokenInvalidateRequest.class));

        // When
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tokenInvalidateRequest)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(CustomResponse.SUCCESS)));

        // Verify
        verify(authService, times(1)).logout(any(TokenInvalidateRequest.class));

    }

    @Test
    void givenTokenInvalidateRequestWithUser_WhenLogoutForUser_ThenReturnInvalidateToken() throws Exception {

        // Given
        TokenInvalidateRequest tokenInvalidateRequest = TokenInvalidateRequest.builder()
                .accessToken("Bearer " + mockUserToken.getAccessToken())
                .refreshToken(mockUserToken.getRefreshToken())
                .build();

        // When
        doNothing().when(authService).logout(any(TokenInvalidateRequest.class));

        // When
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tokenInvalidateRequest)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(CustomResponse.SUCCESS)));

        // Verify
        verify(authService, times(1)).logout(any(TokenInvalidateRequest.class));

    }

}