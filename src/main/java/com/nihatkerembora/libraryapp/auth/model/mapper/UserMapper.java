package com.nihatkerembora.libraryapp.auth.model.mapper;


import com.nihatkerembora.libraryapp.auth.model.User;
import com.nihatkerembora.libraryapp.auth.model.dto.response.UserResponse;
import com.nihatkerembora.libraryapp.auth.model.entity.UserEntity;
import com.nihatkerembora.libraryapp.common.model.CustomPage;
import com.nihatkerembora.libraryapp.common.model.dto.response.CustomPagingResponse;


import java.util.List;
import java.util.stream.Collectors;


public final class UserMapper {

    private UserMapper() { /* prevent instantiation */ }

    /**
     * UserEntity → User
     */
    public static User toDomain(UserEntity e) {
        if (e == null) return null;
        return User.builder()
                .id(e.getId())
                .email(e.getEmail())
                .firstName(e.getFirstName())
                .lastName(e.getLastName())
                .phoneNumber(e.getPhoneNumber())
                .userType(e.getUserType())
                .userStatus(e.getUserStatus())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }

    /**
     * List<UserEntity> → List<User> dönüşümü.
     */
    public static List<User> toDomainList(List<UserEntity> list) {
        if (list == null || list.isEmpty()) return List.of();
        return list.stream()
                .map(UserMapper::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * User → UserResponse
     */
    public static UserResponse toDto(User u) {
        if (u == null) return null;
        return UserResponse.builder()
                .id(u.getId())
                .email(u.getEmail())
                .firstName(u.getFirstName())
                .lastName(u.getLastName())
                .phoneNumber(u.getPhoneNumber())
                .userType(u.getUserType())
                .userStatus(u.getUserStatus())
                .build();
    }

    /**
     * List<User> → List<UserResponse>
     */
    public static List<UserResponse> toDtoList(List<User> list) {
        if (list == null || list.isEmpty()) return List.of();
        return list.stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * CustomPage<User> → CustomPagingResponse<UserResponse>
     */
    public static CustomPagingResponse<UserResponse> toPagingResponse(CustomPage<User> page) {
        if (page == null) return null;
        List<UserResponse> content = toDtoList(page.getContent());
        return CustomPagingResponse.<UserResponse>builder()
                .content(content)
                .pageNumber(page.getPageNumber())
                .pageSize(page.getPageSize())
                .totalElementCount(page.getTotalElementCount())
                .totalPageCount(page.getTotalPageCount())
                .build();
    }
}