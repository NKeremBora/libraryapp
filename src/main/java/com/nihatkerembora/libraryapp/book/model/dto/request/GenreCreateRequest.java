package com.nihatkerembora.libraryapp.book.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GenreCreateRequest {
    @NotBlank
    @Size(max = 50)
    private String name;


    @Size(max = 500)
    private String description;
}

