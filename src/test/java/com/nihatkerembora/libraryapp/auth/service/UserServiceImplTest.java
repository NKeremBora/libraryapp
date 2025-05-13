package com.nihatkerembora.libraryapp.auth.service;


import com.nihatkerembora.libraryapp.auth.exception.UserNotFoundException;
import com.nihatkerembora.libraryapp.auth.exception.UserStatusNotValidException;
import com.nihatkerembora.libraryapp.auth.model.User;
import com.nihatkerembora.libraryapp.auth.model.dto.request.UpdateUserRequest;
import com.nihatkerembora.libraryapp.auth.model.entity.UserEntity;
import com.nihatkerembora.libraryapp.auth.repository.UserRepository;
import com.nihatkerembora.libraryapp.auth.service.impl.UserServiceImpl;
import com.nihatkerembora.libraryapp.base.AbstractBaseServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceImplTest extends AbstractBaseServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    /* ---------- getUserById ---------- */

    @Test
    void getUserById_ShouldThrowWhenUserMissing() {
        // given
        when(userRepository.findById("id1")).thenReturn(Optional.empty());

        // then
        assertThrows(UserNotFoundException.class,
                () -> userService.getUserById("id1", false));

        verify(userRepository).findById("id1");
    }

    @Test
    void getUserById_ShouldReturnDomain() {
        // given
        UserEntity entity = new UserEntity();
        entity.setId("u42");
        entity.setFirstName("Ada");
        when(userRepository.findById("u42")).thenReturn(Optional.of(entity));

        // when
        User result = userService.getUserById("u42", false);

        // then
        assertThat(result.getId()).isEqualTo("u42");
        assertThat(result.getFirstName()).isEqualTo("Ada");
    }


    @Test
    void update_ShouldThrowWhenUserMissing() {
        when(userRepository.findById("missing")).thenReturn(Optional.empty());

        UpdateUserRequest req = new UpdateUserRequest();
        assertThrows(UserNotFoundException.class,
                () -> userService.update("missing", req));
    }

    @Test
    void update_ShouldCopyOnlyNonNullFields() {
        // given – mevcut entity
        UserEntity entity = new UserEntity();
        entity.setId("u5");
        entity.setFirstName("Grace");
        entity.setLastName("Hopper");

        when(userRepository.findById("u5")).thenReturn(Optional.of(entity));
        when(userRepository.save(any(UserEntity.class))).thenAnswer(i -> i.getArgument(0));

        // update isteği (yalnızca lastName null, phone dolu)
        UpdateUserRequest req = new UpdateUserRequest();
        req.setPhoneNumber("555-01");
        req.setLastName(null);

        // when
        User updated = userService.update("u5", req);

        // then
        assertThat(updated.getPhoneNumber()).isEqualTo("555-01");
        assertThat(updated.getLastName()).isEqualTo("Hopper"); // değişmedi
    }

    /* ---------- softDelete ---------- */

    @Test
    void softDelete_ShouldThrowWhenUserMissing() {
        when(userRepository.findById("x")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.softDelete("x"));
    }

    @Test
    void softDelete_ShouldThrowWhenAlreadyDeleted() {
        UserEntity entity = new UserEntity();
        entity.setId("u9");
        entity.setDeleted(true);
        when(userRepository.findById("u9")).thenReturn(Optional.of(entity));

        assertThrows(UserStatusNotValidException.class,
                () -> userService.softDelete("u9"));
    }

    @Test
    void softDelete_ShouldSoftDeleteUser() {
        // given
        UserEntity entity = new UserEntity();
        entity.setId("u10");
        entity.setDeleted(false);

        when(userRepository.findById("u10")).thenReturn(Optional.of(entity));
        when(userRepository.save(any(UserEntity.class))).thenAnswer(i -> i.getArgument(0));

        // when
        userService.softDelete("u10");

        // then
        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().isDeleted()).isTrue();
    }

    @Test
    void update_ShouldNotAlterEntityWhenAllFieldsNull() {
        // Mevcut entity
        UserEntity entity = new UserEntity();
        entity.setId("u7");
        entity.setFirstName("Alan");
        entity.setPhoneNumber("555");

        when(userRepository.findById("u7")).thenReturn(Optional.of(entity));
        when(userRepository.save(any(UserEntity.class))).thenAnswer(i -> i.getArgument(0));

        // Update isteği (her şey null)
        UpdateUserRequest req = new UpdateUserRequest();

        User updated = userService.update("u7", req);

        assertThat(updated.getFirstName()).isEqualTo("Alan");
        assertThat(updated.getPhoneNumber()).isEqualTo("555");
        verify(userRepository).save(entity);
    }

    @Test
    void update_ShouldCopyAllNonNullFields() {
        UserEntity entity = new UserEntity();
        entity.setId("u8");

        when(userRepository.findById("u8")).thenReturn(Optional.of(entity));
        when(userRepository.save(any(UserEntity.class))).thenAnswer(i -> i.getArgument(0));

        UpdateUserRequest req = new UpdateUserRequest();
        req.setFirstName("Tim");
        req.setLastName("Berners-Lee");
        req.setEmail("tim@web.org");
        req.setPhoneNumber("123");

        User updated = userService.update("u8", req);

        assertThat(updated.getFirstName()).isEqualTo("Tim");
        assertThat(updated.getLastName()).isEqualTo("Berners-Lee");
        assertThat(updated.getEmail()).isEqualTo("tim@web.org");
        assertThat(updated.getPhoneNumber()).isEqualTo("123");
    }

    /* ---------- softDelete: repository save() başarısızsa exception propagate ---------- */

    @Test
    @DisplayName("softDelete: save sırasında DB hatası fırlarsa üst kata yansıtılmalı")
    void softDelete_ShouldPropagateRepoException() {
        UserEntity entity = new UserEntity();
        entity.setId("u11");
        entity.setDeleted(false);

        when(userRepository.findById("u11")).thenReturn(Optional.of(entity));
        when(userRepository.save(entity)).thenThrow(new RuntimeException("DB down"));

        assertThrows(RuntimeException.class, () -> userService.softDelete("u11"));
        verify(userRepository).save(entity);
    }

}