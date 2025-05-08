package com.nihatkerembora.libraryapp.auth.model;

import com.nihatkerembora.libraryapp.auth.model.enums.UserStatus;
import com.nihatkerembora.libraryapp.auth.model.enums.UserType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * Domain model representing a user in the system.
 */
@Getter
@Setter
@SuperBuilder
public class User {

    protected String createdUser;
    protected LocalDateTime createdAt;
    protected String updatedUser;
    protected LocalDateTime updatedAt;
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private UserType userType;
    private UserStatus userStatus;
    private boolean isDeleted;

}
