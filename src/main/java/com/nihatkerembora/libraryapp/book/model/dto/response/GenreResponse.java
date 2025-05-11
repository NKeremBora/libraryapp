package com.nihatkerembora.libraryapp.book.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class GenreResponse {
    private String id;
    private String name;
    private String description;
}
