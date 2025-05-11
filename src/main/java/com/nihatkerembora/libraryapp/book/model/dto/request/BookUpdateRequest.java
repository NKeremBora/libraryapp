package com.nihatkerembora.libraryapp.book.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class BookUpdateRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String author;

    @Pattern(regexp = "\\d{10}|\\d{13}")
    private String isbn;

    private LocalDate publicationDate;

    private List<String> genreIds;
}