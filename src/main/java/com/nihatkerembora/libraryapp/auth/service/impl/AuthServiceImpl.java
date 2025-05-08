package com.nihatkerembora.libraryapp.auth.service.impl;


import com.nihatkerembora.libraryapp.auth.exception.PasswordNotValidException;
import com.nihatkerembora.libraryapp.auth.exception.UserAlreadyExistException;
import com.nihatkerembora.libraryapp.auth.exception.UserNotFoundException;
import com.nihatkerembora.libraryapp.auth.exception.UserStatusNotValidException;
import com.nihatkerembora.libraryapp.auth.model.Token;
import com.nihatkerembora.libraryapp.auth.model.User;
import com.nihatkerembora.libraryapp.auth.model.dto.request.LoginRequest;
import com.nihatkerembora.libraryapp.auth.model.dto.request.RegisterRequest;
import com.nihatkerembora.libraryapp.auth.model.dto.request.TokenInvalidateRequest;
import com.nihatkerembora.libraryapp.auth.model.dto.request.TokenRefreshRequest;
import com.nihatkerembora.libraryapp.auth.model.entity.UserEntity;
import com.nihatkerembora.libraryapp.auth.model.enums.TokenClaims;
import com.nihatkerembora.libraryapp.auth.model.enums.UserStatus;
import com.nihatkerembora.libraryapp.auth.model.mapper.RegisterRequestMapper;
import com.nihatkerembora.libraryapp.auth.model.mapper.UserMapper;
import com.nihatkerembora.libraryapp.auth.repository.UserRepository;
import com.nihatkerembora.libraryapp.auth.service.AuthService;
import com.nihatkerembora.libraryapp.auth.service.InvalidTokenService;
import com.nihatkerembora.libraryapp.auth.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final InvalidTokenService invalidTokenService;



    /**
     * Authenticates the user based on the login request and issues a new {@link Token}.
     *
     * @param loginRequest the request containing login credentials
     * @return a {@link Token} if authentication is successful
     */
    @Override
    public Token login(LoginRequest loginRequest) {

        final UserEntity userEntityFromDB = userRepository
                .findByEmail(loginRequest.getEmail())
                .orElseThrow(
                        () -> new UserNotFoundException(loginRequest.getEmail())
                );

        if (Boolean.FALSE.equals(passwordEncoder.matches(
                loginRequest.getPassword(), userEntityFromDB.getPassword()))) {
            throw new PasswordNotValidException();
        }

        return tokenService.generateToken(userEntityFromDB.getClaims());
    }


    /**
     * Invalidates the user's tokens during logout.
     *
     * @param tokenInvalidateRequest the request containing token IDs to invalidate
     */
    @Override
    public void logout(TokenInvalidateRequest tokenInvalidateRequest) {

        tokenService.verifyAndValidate(
                Set.of(
                        tokenInvalidateRequest.getAccessToken(),
                        tokenInvalidateRequest.getRefreshToken()
                )
        );

        final String accessTokenId = tokenService
                .getPayload(tokenInvalidateRequest.getAccessToken())
                .getId();

        invalidTokenService.checkForInvalidityOfToken(accessTokenId);


        final String refreshTokenId = tokenService
                .getPayload(tokenInvalidateRequest.getRefreshToken())
                .getId();

        invalidTokenService.checkForInvalidityOfToken(refreshTokenId);

        invalidTokenService.invalidateTokens(Set.of(accessTokenId,refreshTokenId));

    }
    /**
     * Generates a new access token using the provided refresh token.
     *
     * @param tokenRefreshRequest the request containing the refresh token
     * @return a new {@link Token} containing the refreshed access and refresh tokens
     */
    @Override
    public Token refreshToken(TokenRefreshRequest tokenRefreshRequest) {

        tokenService.verifyAndValidate(tokenRefreshRequest.getRefreshToken());

        final String adminId = tokenService
                .getPayload(tokenRefreshRequest.getRefreshToken())
                .get(TokenClaims.USER_ID.getValue())
                .toString();

        final UserEntity userEntityFromDB = userRepository
                .findById(adminId)
                .orElseThrow(UserNotFoundException::new);

        this.validateAdminStatus(userEntityFromDB);

        return tokenService.generateToken(
                userEntityFromDB.getClaims(),
                tokenRefreshRequest.getRefreshToken()
        );
    }


    /**
     * Registers a new user based on the provided registration request.
     *
     * @param registerRequest the request containing user registration details
     * @return the newly registered {@link User}
     */
    @Override
    public User registerUser(RegisterRequest registerRequest) {

        if (userRepository.existsByEmailAndIsDeletedFalse(registerRequest.getEmail())) {
            throw new UserAlreadyExistException("The email is already used for another user : " + registerRequest.getEmail());
        }

        final UserEntity userEntityToBeSaved = RegisterRequestMapper.toEntity(registerRequest);

        userEntityToBeSaved.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        final UserEntity savedUserEntity = userRepository.save(userEntityToBeSaved);

        return UserMapper.toDomain(savedUserEntity);

    }
    private void validateAdminStatus(final UserEntity userEntity) {
        if (!(UserStatus.ACTIVE.equals(userEntity.getUserStatus()))) {
            throw new UserStatusNotValidException("UserStatus = " + userEntity.getUserStatus());
        }
    }
}
