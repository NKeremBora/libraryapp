package com.nihatkerembora.libraryapp.borrow.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a book is not available for borrowing.
 */
public class BookNotAvailableException extends RuntimeException {

    public static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;

    private static final String DEFAULT_MESSAGE = """
            Book is not available!
            """;

    public BookNotAvailableException() {
        super(DEFAULT_MESSAGE);
    }

    public BookNotAvailableException(final String message) {
        super(DEFAULT_MESSAGE + " " + message);
    }
}
