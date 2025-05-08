package com.nihatkerembora.libraryapp.book.exception;


import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a book cannot be found by its ID.
 */
public class BookNotFoundException extends RuntimeException {

    public static final HttpStatus STATUS = HttpStatus.NOT_FOUND;

    private static final String DEFAULT_MESSAGE = "Book not found!";

    public BookNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public BookNotFoundException(final String message) {
        super(DEFAULT_MESSAGE + " " + message);
    }
}
