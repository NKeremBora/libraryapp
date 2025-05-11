package com.nihatkerembora.libraryapp.auth.port.in;

import com.nihatkerembora.libraryapp.auth.exception.UserNotFoundException;
import com.nihatkerembora.libraryapp.auth.model.entity.UserEntity;
import com.nihatkerembora.libraryapp.auth.model.enums.UserStatus;
import com.nihatkerembora.libraryapp.auth.repository.UserRepository;
import com.nihatkerembora.libraryapp.borrowing.port.out.UserStatusPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class UserStatusAdapter implements UserStatusPort {

    private final UserRepository userRepository;

    @Override
    public boolean isActive(String userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        return user.getUserStatus() == UserStatus.ACTIVE;
    }
}
