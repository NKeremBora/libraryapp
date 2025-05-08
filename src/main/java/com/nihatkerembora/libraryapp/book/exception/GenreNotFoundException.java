package com.nihatkerembora.libraryapp.book.exception;

import org.springframework.http.HttpStatus;

import java.util.UUID;

public class GenreNotFoundException extends RuntimeException {

    public static final HttpStatus STATUS = HttpStatus.NOT_FOUND;

    private static final String DEFAULT_MESSAGE = "Genre not found with id ";

    public GenreNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public GenreNotFoundException(final UUID message) {
        super(DEFAULT_MESSAGE + " " + message);
    }
}