package com.nihatkerembora.libraryapp.auth.model.mapper;

import com.nihatkerembora.libraryapp.auth.model.dto.request.RegisterRequest;
import com.nihatkerembora.libraryapp.auth.model.entity.UserEntity;

/**
 * Basit utility sınıfı: RegisterRequest → UserEntity dönüşümü
 */
public final class RegisterRequestMapper {

    private RegisterRequestMapper() {
        // instance creation prevented
    }

    /**
     * Converts a RegisterRequest into a UserEntity ready for persistence.
     *
     * @param req the incoming DTO
     * @return a new UserEntity populated from req
     */
    public static UserEntity toEntity(RegisterRequest req) {
        if (req == null) {
            return null;
        }
        return UserEntity.builder()
                .email(req.getEmail())
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .phoneNumber(req.getPhoneNumber())
                .userType(req.getUserType())
                .build();
    }
}