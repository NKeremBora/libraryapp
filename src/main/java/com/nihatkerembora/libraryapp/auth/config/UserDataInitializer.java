package com.nihatkerembora.libraryapp.auth.config;

import com.nihatkerembora.libraryapp.auth.model.entity.UserEntity;
import com.nihatkerembora.libraryapp.auth.model.enums.UserStatus;
import com.nihatkerembora.libraryapp.auth.model.enums.UserType;
import com.nihatkerembora.libraryapp.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
public class UserDataInitializer implements CommandLineRunner {

    private final UserRepository userRepo;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepo.count() == 0) {
            UserEntity admin = UserEntity.builder()
                    .email("admin@example.com")
                    .password("secureAdminPass")
                    .firstName("Admin")
                    .lastName("User")
                    .userType(UserType.ADMIN)
                    .userStatus(UserStatus.ACTIVE)
                    .build();

            UserEntity user = UserEntity.builder()
                    .email("user@example.com")
                    .password("secureUserPass")
                    .firstName("Normal")
                    .lastName("User")
                    .userType(UserType.USER)
                    .userStatus(UserStatus.ACTIVE)
                    .build();

            userRepo.saveAll(List.of(admin, user));
            log.info(">>>>> Initial users seeded: admin={}, user={}", admin.getId(), user.getId());
        }
    }
}
