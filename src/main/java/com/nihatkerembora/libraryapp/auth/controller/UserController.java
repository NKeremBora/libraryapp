package com.nihatkerembora.libraryapp.auth.controller;

import com.nihatkerembora.libraryapp.auth.model.User;
import com.nihatkerembora.libraryapp.auth.model.dto.request.CustomPaging;
import com.nihatkerembora.libraryapp.auth.model.dto.request.CustomPagingRequest;
import com.nihatkerembora.libraryapp.auth.model.dto.request.UpdateUserRequest;
import com.nihatkerembora.libraryapp.auth.model.dto.response.UserResponse;
import com.nihatkerembora.libraryapp.auth.model.mapper.UserMapper;
import com.nihatkerembora.libraryapp.auth.service.UserService;
import com.nihatkerembora.libraryapp.common.model.dto.response.CustomPagingResponse;
import com.nihatkerembora.libraryapp.common.model.dto.response.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Handles user management, including creation, updates, and soft-deletion.")
public class UserController {

    private final UserService userService;

    /**
     * Retrieves a user by ID.
     *
     * @param id the ID of the user to retrieve
     * @return a {@link CustomResponse} containing the user's data
     */
    @Operation(
            summary = "Get User by ID",
            description = "Retrieves user data by ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User found"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @PreAuthorize("@securityService.canAccessUser(authentication, #id)")
    @GetMapping("/{id}")
    public CustomResponse<UserResponse> getUser(@PathVariable String id,
                                                @RequestParam(defaultValue = "false") boolean includeDeleted) {
        User user = userService.getUserById(id, includeDeleted);
        UserResponse userResponse = UserMapper.toDto(user);
        return CustomResponse.successOf(userResponse);
    }

    /**
     * Retrieves a paginated list of users.
     *
     * @param pageNumber token for pagination
     * @param pageSize token for pagination
     * @param includeDeleted whether to include deleted users
     * @return a {@link CustomResponse} containing the paginated user list
     */
    @Operation(
            summary = "Get Paginated Users",
            description = "Retrieves a paginated list of users.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
            }
    )
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping
    public CustomResponse<CustomPagingResponse<UserResponse>> listUsers(
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(defaultValue = "false") boolean includeDeleted) {

        CustomPaging paging = CustomPaging.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .build();

        CustomPagingRequest pagingRequest = CustomPagingRequest.builder()
                .pagination(paging)
                .build();

        var page = userService.getUsers(pagingRequest, includeDeleted);

        CustomPagingResponse<UserResponse> response = UserMapper.toPagingResponse(page);


        return CustomResponse.successOf(response);
    }

    /**
     * Updates user information by ID.
     *
     * @param id the ID of the user to update
     * @param request the user update details
     * @return a {@link CustomResponse} containing the updated user data
     */
    @Operation(
            summary = "Update User",
            description = "Updates user details.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User updated successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PutMapping("/{id}")
    public CustomResponse<UserResponse> updateUser(@PathVariable String id,
                                                   @RequestBody @Valid UpdateUserRequest request) {
        User updatedUser = userService.update(id, request);
        UserResponse userResponse = UserMapper.toDto(updatedUser);
        return CustomResponse.successOf(userResponse);
    }

    /**
     * Soft deletes a user by ID.
     *
     * @param id the ID of the user to delete
     * @return a {@link CustomResponse} indicating success
     */
    @Operation(
            summary = "Soft Delete User",
            description = "Marks a user as deleted by ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User soft deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}/soft-delete")
    public CustomResponse<String> deleteUser(@PathVariable String id) {
        userService.softDelete(id);
        return CustomResponse.successOf("User with ID " + id + " is soft deleted");
    }
}