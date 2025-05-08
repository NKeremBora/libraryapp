package com.nihatkerembora.libraryapp.book.exception;

import org.springframework.http.HttpStatus;

public class IsbnAlreadyExistsException extends RuntimeException {

    public static final HttpStatus STATUS = HttpStatus.CONFLICT;

    private static final String DEFAULT_MESSAGE = "ISBN already exists for another book.";

    public IsbnAlreadyExistsException() {
        super(DEFAULT_MESSAGE);
    }

    public IsbnAlreadyExistsException(final String message) {
        super(message);
    }
}
