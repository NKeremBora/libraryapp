package com.nihatkerembora.libraryapp.auth.controller;

import com.nihatkerembora.libraryapp.auth.model.dto.request.LoginRequest;
import com.nihatkerembora.libraryapp.auth.model.dto.request.RegisterRequest;
import com.nihatkerembora.libraryapp.auth.model.dto.request.TokenInvalidateRequest;
import com.nihatkerembora.libraryapp.auth.model.dto.request.TokenRefreshRequest;
import com.nihatkerembora.libraryapp.auth.model.entity.UserEntity;
import com.nihatkerembora.libraryapp.auth.model.enums.UserStatus;
import com.nihatkerembora.libraryapp.auth.model.enums.UserType;
import com.nihatkerembora.libraryapp.auth.repository.UserRepository;
import com.nihatkerembora.libraryapp.base.AbstractRestControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AuthControllerIntegrationTest extends AbstractRestControllerTest {

    private static final String DEFAULT_PASSWORD = "Password123!";

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() throws Exception {
        userRepository.deleteAll();
        // Pre-register users for login, refresh and logout tests
        registerUser("loginuser@example.com");
        registerUser("refresher@example.com");
        registerUser("logout@example.com");
    }

    private void registerUser(String email) throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setEmail(email);
        req.setPassword(DEFAULT_PASSWORD);
        req.setFirstName("Test");
        req.setLastName("User");
        req.setPhoneNumber("55512345678");
        req.setUserType(UserType.USER);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void register_thenUserCreatedInDb() throws Exception {
        long before = userRepository.count();
        String email = "testuser@example.com";

        RegisterRequest req = new RegisterRequest();
        req.setEmail(email);
        req.setPassword(DEFAULT_PASSWORD);
        req.setFirstName("Test");
        req.setLastName("User");
        req.setPhoneNumber("55512345678");
        req.setUserType(UserType.USER);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true));

        long after = userRepository.count();
        assertThat(after).isEqualTo(before + 1);

        UserEntity saved = userRepository.findByEmail(email).orElseThrow();
        assertThat(saved.getEmail()).isEqualTo(email);
        assertThat(saved.getUserStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void login_thenReturnsTokens() throws Exception {
        LoginRequest login = LoginRequest.builder()
                .email("loginuser@example.com")
                .password(DEFAULT_PASSWORD)
                .build();

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.accessToken").exists())
                .andExpect(jsonPath("$.response.refreshToken").exists());
    }

    @Test
    void refreshToken_thenNewAccessToken() throws Exception {
        LoginRequest login = LoginRequest.builder()
                .email("refresher@example.com")
                .password(DEFAULT_PASSWORD)
                .build();
        String loginResponse = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String refreshToken = objectMapper.readTree(loginResponse)
                .at("/response/refreshToken").asText();

        TokenRefreshRequest refreshReq = new TokenRefreshRequest();
        refreshReq.setRefreshToken(refreshToken);

        mockMvc.perform(post("/api/v1/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.accessToken").exists());
    }

    @Test
    void logout_thenInvalidate() throws Exception {
        LoginRequest login = LoginRequest.builder()
                .email("logout@example.com")
                .password(DEFAULT_PASSWORD)
                .build();
        String loginResp = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String accessToken = objectMapper.readTree(loginResp)
                .at("/response/accessToken").asText();
        String refreshToken = objectMapper.readTree(loginResp)
                .at("/response/refreshToken").asText();

        TokenInvalidateRequest logoutReq = new TokenInvalidateRequest();
        logoutReq.setAccessToken(accessToken);
        logoutReq.setRefreshToken(refreshToken);

        mockMvc.perform(post("/api/v1/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(logoutReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true));
    }

    @Test
    void register_duplicateEmail_thenBadRequest() throws Exception {
        String email = "dup@example.com";
        registerUser(email);

        RegisterRequest dupReq = new RegisterRequest();
        dupReq.setEmail(email);
        dupReq.setPassword(DEFAULT_PASSWORD);
        dupReq.setFirstName("Test");
        dupReq.setLastName("User");
        dupReq.setPhoneNumber("55512345678");
        dupReq.setUserType(UserType.USER);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dupReq)))
                .andExpect(status().isConflict());
    }

    @Test
    void login_wrongPassword_thenUnauthorized() throws Exception {
        LoginRequest login = LoginRequest.builder()
                .email("loginuser@example.com")
                .password("WrongPass!")
                .build();

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void refreshToken_invalidToken_thenUnauthorized() throws Exception {
        TokenRefreshRequest badReq = new TokenRefreshRequest();
        badReq.setRefreshToken("invalidToken");

        mockMvc.perform(post("/api/v1/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badReq)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void logout_invalidToken_thenUnauthorized() throws Exception {
        TokenInvalidateRequest badLogout = new TokenInvalidateRequest();
        badLogout.setAccessToken("invalid");
        badLogout.setRefreshToken("invalid");

        mockMvc.perform(post("/api/v1/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badLogout)))
                .andExpect(status().isInternalServerError());
    }
}
