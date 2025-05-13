package com.nihatkerembora.libraryapp.book.service;

import com.nihatkerembora.libraryapp.auth.exception.UserNotFoundException;
import com.nihatkerembora.libraryapp.auth.model.entity.UserEntity;
import com.nihatkerembora.libraryapp.auth.model.enums.UserStatus;
import com.nihatkerembora.libraryapp.auth.port.in.UserStatusAdapter;
import com.nihatkerembora.libraryapp.auth.repository.UserRepository;
import com.nihatkerembora.libraryapp.base.AbstractBaseServiceTest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserStatusAdapterTest extends AbstractBaseServiceTest {

    @InjectMocks
    private UserStatusAdapter adapter;

    @Mock
    private UserRepository userRepository;

    /* ---------- isActive ---------- */

    @Test
    void isActive_ShouldReturnTrue_WhenUserActive() {
        // given
        UserEntity active = new UserEntity();
        active.setId("u1");
        active.setUserStatus(UserStatus.ACTIVE);

        when(userRepository.findById("u1")).thenReturn(Optional.of(active));

        // when–then
        assertThat(adapter.isActive("u1")).isTrue();
        verify(userRepository).findById("u1");
    }

    @Test
    void isActive_ShouldReturnFalse_WhenUserNotActive() {
        // given
        UserEntity passive = new UserEntity();
        passive.setId("u2");
        passive.setUserStatus(UserStatus.SUSPENDED);

        when(userRepository.findById("u2")).thenReturn(Optional.of(passive));

        // when–then
        assertThat(adapter.isActive("u2")).isFalse();
        verify(userRepository).findById("u2");
    }

    @Test
    void isActive_ShouldThrow_WhenUserMissing() {
        when(userRepository.findById("x")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> adapter.isActive("x"));

        verify(userRepository).findById("x");
    }
}
