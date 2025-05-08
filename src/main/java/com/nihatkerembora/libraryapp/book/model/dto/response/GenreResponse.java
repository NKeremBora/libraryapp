package com.nihatkerembora.libraryapp.book.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class GenreResponse {
    private UUID id;
    private String name;
    private String description;
}
