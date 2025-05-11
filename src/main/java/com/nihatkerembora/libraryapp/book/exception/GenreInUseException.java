package com.nihatkerembora.libraryapp.book.exception;

import org.springframework.http.HttpStatus;

public class GenreInUseException extends RuntimeException {

    public static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;

    private static final String DEFAULT_MESSAGE = "Genre is in use and cannot be deleted.";

    public GenreInUseException() {
        super(DEFAULT_MESSAGE);
    }

    public GenreInUseException(final String message) {
        super(DEFAULT_MESSAGE + " " + message);
    }
}