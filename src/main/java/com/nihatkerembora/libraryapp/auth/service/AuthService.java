package com.nihatkerembora.libraryapp.auth.service;


import com.nihatkerembora.libraryapp.auth.model.Token;
import com.nihatkerembora.libraryapp.auth.model.User;
import com.nihatkerembora.libraryapp.auth.model.dto.request.RegisterRequest;
import com.nihatkerembora.libraryapp.auth.model.dto.request.TokenInvalidateRequest;
import com.nihatkerembora.libraryapp.auth.model.dto.request.TokenRefreshRequest;
import com.nihatkerembora.libraryapp.auth.model.dto.request.LoginRequest;


public interface AuthService {
    /**
     * Authenticates the user based on the login request and issues a new {@link Token}.
     *
     * @param loginRequest the request containing login credentials
     * @return a {@link Token} if authentication is successful
     */
    Token login(final LoginRequest loginRequest);

    /**
     * Invalidates the user's tokens during logout.
     *
     * @param tokenInvalidateRequest the request containing token IDs to invalidate
     */
    void logout(final TokenInvalidateRequest tokenInvalidateRequest);

    /**
     * Registers a new user based on the provided registration request.
     *
     * @param registerRequest the request containing user registration details
     * @return the newly registered {@link User}
     */
    User registerUser(final RegisterRequest registerRequest);

    /**
     * Generates a new access token using the provided refresh token.
     *
     * @param tokenRefreshRequest the request containing the refresh token
     * @return a new {@link Token} containing the refreshed access and refresh tokens
     */
    Token refreshToken(final TokenRefreshRequest tokenRefreshRequest);

}

