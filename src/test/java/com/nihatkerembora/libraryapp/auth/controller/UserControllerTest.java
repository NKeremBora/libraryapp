package com.nihatkerembora.libraryapp.auth.controller;


import com.nihatkerembora.libraryapp.auth.exception.UserNotFoundException;
import com.nihatkerembora.libraryapp.auth.model.User;
import com.nihatkerembora.libraryapp.auth.model.dto.request.CustomPagingRequest;
import com.nihatkerembora.libraryapp.auth.model.dto.request.UpdateUserRequest;
import com.nihatkerembora.libraryapp.auth.model.enums.UserType;
import com.nihatkerembora.libraryapp.auth.service.UserService;
import com.nihatkerembora.libraryapp.base.AbstractRestControllerTest;
import com.nihatkerembora.libraryapp.builder.UserIdentityBuilder;
import com.nihatkerembora.libraryapp.common.model.CustomPage;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends AbstractRestControllerTest {

    @MockitoBean
    UserService userService;

    @MockitoBean
    UserIdentityBuilder userIdentityBuilder;

    private final String BASE_URL = "/api/v1/users";

    @Test
    void givenValidId_whenGetUser_thenReturnUserResponse() throws Exception {

        final String userId = userIdentityBuilder.extractUserIdFromToken(mockAdminToken.getAccessToken());
        // Given
        User user = User.builder()
                .id(userId)
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .userType(UserType.USER)
                .build();

        when(userService.getUserById(userId, false)).thenReturn(user);

        // Then
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/123")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockAdminToken.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true));
    }

    @Test
    void givenValidPagingRequest_whenListUsers_thenReturnPagedResponse() throws Exception {
        // Given
        User user = User.builder()
                .id("1")
                .email("page@example.com")
                .firstName("Paged")
                .lastName("User")
                .userType(UserType.USER)
                .build();

        List<User> userList = List.of(user);
        CustomPage<User> userPage = CustomPage.of(userList, new PageImpl<>(userList));

        when(userService.getUsers(any(CustomPagingRequest.class), eq(false)))
                .thenReturn(userPage);

        // Then
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL)
                        .param("pageNumber", "0")
                        .param("pageSize", "10")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockAdminToken.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true));
    }

    @Test
    void givenValidUpdateRequest_whenUpdateUser_thenReturnUpdatedUser() throws Exception {
        // Given
        UpdateUserRequest request = UpdateUserRequest.builder()
                .firstName("Updated")
                .lastName("User")
                .phoneNumber("12345678904")
                .build();

        User updatedUser = User.builder()
                .id("123")
                .email("test@example.com")
                .firstName("Updated")
                .lastName("User")
                .phoneNumber("12345678904")
                .userType(UserType.USER)
                .build();

        when(userService.update(eq("123"), any(UpdateUserRequest.class)))
                .thenReturn(updatedUser);

        // Then
        mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockAdminToken.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.response.firstName").value("Updated"));
    }

    @Test
    void givenUserId_whenSoftDeleteUser_thenReturnSuccess() throws Exception {
        // Given
        doNothing().when(userService).softDelete("123");

        // Then
        mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/123/soft-delete")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockAdminToken.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.response").value("User with ID 123 is soft deleted"));
    }

    // 1) GET /{id} — Kayıt bulunamadı
    @Test
    void givenNonExistingId_whenGetUser_thenReturnNotFound() throws Exception {
        // Servis katmanını, bulunamadığı durumu fırlatacak şekilde stub’luyoruz.
        when(userService.getUserById("999", false))
                .thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/999")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockAdminToken.getAccessToken()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.httpStatus").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("User not found! User not found"));
    }

    // --------------------
    // 2) GET list — ADMIN olmayan kullanıcı → 403 Forbidden
    @Test
    void givenNonAdminToken_whenListUsers_thenReturnForbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL)
                        .param("pageNumber", "0")
                        .param("pageSize", "10")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockUserToken.getAccessToken()))
                .andExpect(status().isForbidden());
    }

    // --------------------
    // 3) GET list — Yetkisiz (Token yok) → 401 Unauthorized
    @Test
    void givenNoToken_whenListUsers_thenReturnUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL)
                        .param("pageNumber", "0")
                        .param("pageSize", "10"))
                .andExpect(status().isUnauthorized());
    }

    // --------------------
    // 4) PUT update — Geçersiz telefon numarası → 400 Bad Request
    @Test
    void givenInvalidPhoneNumber_whenUpdateUser_thenReturnBadRequest() throws Exception {
        // 10 haneli, min 11 haneyi sağlamıyor
        UpdateUserRequest badRequest = UpdateUserRequest.builder()
                .firstName("Foo")
                .lastName("Bar")
                .phoneNumber("1234567890")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badRequest))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockAdminToken.getAccessToken()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.subErrors[0].field").value("phoneNumber"));
    }

    // --------------------
    // 5) PUT update — ADMIN olmayan kullanıcı → 403 Forbidden
    @Test
    void givenNonAdminToken_whenUpdateUser_thenReturnForbidden() throws Exception {
        UpdateUserRequest req = UpdateUserRequest.builder()
                .firstName("X")
                .lastName("Y")
                .phoneNumber("12345678901")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockUserToken.getAccessToken()))
                .andExpect(status().isForbidden());
    }

    // --------------------
    // 6) PUT update — Kayıt bulunamadı → 404 Not Found
    @Test
    void givenNonExistingId_whenUpdateUser_thenReturnNotFound() throws Exception {
        UpdateUserRequest req = UpdateUserRequest.builder()
                .firstName("X")
                .lastName("Y")
                .phoneNumber("12345678901")
                .build();

        when(userService.update(eq("999"), any(UpdateUserRequest.class)))
                .thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockAdminToken.getAccessToken()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found! User not found"));
    }

    // --------------------
    // 7) PUT soft-delete — ADMIN olmayan kullanıcı → 403 Forbidden
    @Test
    void givenNonAdminToken_whenSoftDeleteUser_thenReturnForbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/123/soft-delete")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockUserToken.getAccessToken()))
                .andExpect(status().isForbidden());
    }

    // --------------------
    // 8) PUT soft-delete — Kayıt bulunamadı → 404 Not Found
    @Test
    void givenNonExistingId_whenSoftDeleteUser_thenReturnNotFound() throws Exception {
        doThrow(new UserNotFoundException("User not found")).when(userService).softDelete("999");

        mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/999/soft-delete")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockAdminToken.getAccessToken()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found! User not found"));
    }
}
