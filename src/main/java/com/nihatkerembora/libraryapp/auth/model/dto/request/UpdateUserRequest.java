package com.nihatkerembora.libraryapp.auth.model.dto.request;

import com.nihatkerembora.libraryapp.auth.model.enums.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Request DTO used for user registration.
 * Contains user credentials and profile details required to create a new account.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserRequest {

    @Email(message = "Please enter a valid e-mail address")
    @Size(min = 7, message = "Minimum e-mail length is 7 characters.")
    private String email;

    private String firstName;

    private String lastName;

    @Size(min = 11, max = 20, message = "Phone number must be between 11 and 20 characters.")
    private String phoneNumber;

    private UserType userType;

}
