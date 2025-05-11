package com.nihatkerembora.libraryapp.book.model.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GenreUpdateRequest {
    @Size(max = 50)
    private String code;

    @Size(max = 100)
    private String name;

    @Size(max = 500)
    private String description;
}
