package com.nihatkerembora.libraryapp.auth.service;

import com.nihatkerembora.libraryapp.auth.exception.UserAlreadyExistException;
import com.nihatkerembora.libraryapp.auth.model.User;
import com.nihatkerembora.libraryapp.auth.model.dto.request.RegisterRequest;
import com.nihatkerembora.libraryapp.auth.model.entity.UserEntity;
import com.nihatkerembora.libraryapp.auth.model.enums.UserType;
import com.nihatkerembora.libraryapp.auth.model.mapper.RegisterRequestMapper;
import com.nihatkerembora.libraryapp.auth.model.mapper.UserMapper;
import com.nihatkerembora.libraryapp.auth.repository.UserRepository;
import com.nihatkerembora.libraryapp.auth.service.impl.AuthServiceImpl;
import com.nihatkerembora.libraryapp.base.AbstractBaseServiceTest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceImplRegisterTest extends AbstractBaseServiceTest {

    @InjectMocks
    private AuthServiceImpl registerService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;


    @Test
    void givenAdminRegisterRequest_whenRegisterAdmin_thenReturnAdmin() {

        // Given
        final RegisterRequest request = RegisterRequest.builder()
                .email("usertest@example.com")
                .password("password123")
                .firstName("User FirstName")
                .lastName("User LastName")
                .userType(UserType.ADMIN)
                .phoneNumber("1234567890")
                .build();

        final String encodedPassword = "encodedPassword";

        final UserEntity userEntity = RegisterRequestMapper.toEntity(request);

        final User expected = UserMapper.toDomain(userEntity);

        // When
        when(userRepository.existsByEmailAndIsDeletedFalse(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn(encodedPassword);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        // Then
        User result = registerService.registerUser(request);

        assertEquals(expected.getId(), result.getId());
        assertEquals(expected.getEmail(), result.getEmail());
        assertEquals(expected.getPhoneNumber(), result.getPhoneNumber());
        assertEquals(expected.getFirstName(), result.getFirstName());
        assertEquals(expected.getLastName(), result.getLastName());

        // Verify
        verify(userRepository).save(any(UserEntity.class));

    }

    @Test
    void givenAdminRegisterRequest_whenEmailAlreadyExists_thenThrowAdminAlreadyExistException() {

        // Given
        final RegisterRequest request = RegisterRequest.builder()
                .email("usertest@example.com")
                .password("password123")
                .firstName("User FirstName")
                .lastName("User LastName")
                .userType(UserType.ADMIN)
                .phoneNumber("1234567890")
                .build();

        // When
        when(userRepository.existsByEmailAndIsDeletedFalse(request.getEmail())).thenReturn(true);

        // Then
        assertThrows(UserAlreadyExistException.class, () -> registerService.registerUser(request));

        // Verify
        verify(userRepository, never()).save(any(UserEntity.class));

    }

}
