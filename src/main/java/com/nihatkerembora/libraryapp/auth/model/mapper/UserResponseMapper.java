package com.nihatkerembora.libraryapp.auth.model.mapper;

import com.nihatkerembora.libraryapp.auth.model.User;
import com.nihatkerembora.libraryapp.auth.model.dto.response.UserResponse;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility sınıfı: User → UserResponse dönüşümü
 */
public final class UserResponseMapper {

    private UserResponseMapper() {
        // Instantiation prevented
    }

    /**
     * Tekil User → UserResponse dönüştürme.
     *
     * @param user domain model
     * @return UserResponse DTO, user null ise null döner
     */
    public static UserResponse toDto(User user) {
        if (user == null) {
            return null;
        }
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .userType(user.getUserType())
                .userStatus(user.getUserStatus())
                .build();
    }

    /**
     * Liste halinde User → List<UserResponse> dönüştürme.
     *
     * @param users domain model listesi
     * @return DTO listesi (null yerine boş liste döner)
     */
    public static List<UserResponse> toDtoList(List<User> users) {
        if (users == null || users.isEmpty()) {
            return List.of();
        }
        return users.stream()
                .map(UserResponseMapper::toDto)
                .collect(Collectors.toList());
    }
}