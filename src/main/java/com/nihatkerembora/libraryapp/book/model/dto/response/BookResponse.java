package com.nihatkerembora.libraryapp.book.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookResponse {
    private String id;
    private String title;
    private String author;
    private String isbn;
    private LocalDate publicationDate;
    private List<String> genres;
    private String status;
}
