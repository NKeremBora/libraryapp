package com.nihatkerembora.libraryapp.book.model.mapper;


import com.nihatkerembora.libraryapp.book.model.dto.request.BookCreateRequest;
import com.nihatkerembora.libraryapp.book.model.dto.request.BookUpdateRequest;
import com.nihatkerembora.libraryapp.book.model.dto.response.BookResponse;
import com.nihatkerembora.libraryapp.book.model.entity.Book;
import com.nihatkerembora.libraryapp.book.model.entity.Genre;
import com.nihatkerembora.libraryapp.book.model.enums.Status;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BookMapper {

    public Book toEntity(BookCreateRequest req, Set<Genre> genres) {
        return Book.builder()
                .title(req.getTitle())
                .author(req.getAuthor())
                .isbn(req.getIsbn())
                .publicationDate(req.getPublicationDate())
                .genres(genres)
                .status(Status.AVAILABLE)
                .build();
    }

    public BookResponse toDto(Book book) {
        BookResponse resp = new BookResponse();
        resp.setId(book.getId());
        resp.setTitle(book.getTitle());
        resp.setAuthor(book.getAuthor());
        resp.setIsbn(book.getIsbn());
        resp.setPublicationDate(book.getPublicationDate());
        resp.setGenres(book.getGenres().stream()
                .map(Genre::getName)
                .collect(Collectors.toList()));
        resp.setStatus(book.getStatus().name());
        return resp;
    }

    public Book toEntity(BookUpdateRequest req, Set<Genre> genres) {
        return Book.builder()
                .title(req.getTitle())
                .author(req.getAuthor())
                .isbn(req.getIsbn())
                .publicationDate(req.getPublicationDate())
                .genres(genres)
                .status(Status.AVAILABLE)
                .build();
    }
}