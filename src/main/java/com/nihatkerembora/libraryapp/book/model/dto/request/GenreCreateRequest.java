package com.nihatkerembora.libraryapp.book.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class GenreCreateRequest {
    @NotBlank
    @Size(max = 50)
    private String name;


    @Size(max = 500)
    private String description;
}

