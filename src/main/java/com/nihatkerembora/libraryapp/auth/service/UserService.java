package com.nihatkerembora.libraryapp.auth.service;


import com.nihatkerembora.libraryapp.auth.model.User;
import com.nihatkerembora.libraryapp.auth.model.dto.request.CustomPagingRequest;
import com.nihatkerembora.libraryapp.auth.model.dto.request.UpdateUserRequest;

import com.nihatkerembora.libraryapp.common.model.CustomPage;
import org.springframework.data.domain.Page;

/**
 * Service interface to manage user-related operations.
 * Provides methods for retrieving, updating, and deleting user data.
 */
public interface UserService {

    /**
     * Retrieves a user by their ID.
     *
     * @param id The unique identifier of the user.
     * @param includeDeleted If true, includes deleted users in the response.
     * @return The user data wrapped in a UserDto object.
     */
    User getUserById(String id, boolean includeDeleted);

    /**
     * Retrieves a paginated list of users.
     *
     * @param pagingRequest The number of users per page.
     * @param includeDeleted If true, includes deleted users in the response.
     * @return A paginated list of users wrapped in a Response object.
     */
    CustomPage<User> getUsers(CustomPagingRequest pagingRequest, boolean includeDeleted);

    /**
     * Updates the details of an existing user.
     *
     * @param id The unique identifier of the user to update.
     * @param request The new data for the user encapsulated in an UpdateUserRequest object.
     * @return The updated user data wrapped in a Response object.
     */
    User update(String id, UpdateUserRequest request);

    /**
     * Soft deletes a user, marking them as deleted without removing their data.
     *
     * @param id The unique identifier of the user to delete.
     */
    void softDelete(String id);
}

