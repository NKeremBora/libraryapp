package com.nihatkerembora.libraryapp.auth.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Request DTO used to invalidate access and refresh tokens during logout.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenInvalidateRequest {

    @NotBlank
    private String accessToken;

    @NotBlank
    private String refreshToken;

}
