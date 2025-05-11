package com.nihatkerembora.libraryapp.book.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Builder
@Data
public class BookCreateRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String author;

    @Pattern(regexp = "\\d{10}|\\d{13}")
    private String isbn;

    @NotNull
    private LocalDate publicationDate;


    private List<String> genreIds;
}