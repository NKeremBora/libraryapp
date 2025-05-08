package com.nihatkerembora.libraryapp.book.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when attempting to create a book whose ISBN is already registered.
 */
public class BookAlreadyExistException extends RuntimeException {

    public static final HttpStatus STATUS = HttpStatus.CONFLICT;

    private static final String DEFAULT_MESSAGE = "Book with given ISBN already exists!";

    public BookAlreadyExistException() {
        super(DEFAULT_MESSAGE);
    }

    public BookAlreadyExistException(final String message) {
        super(DEFAULT_MESSAGE + " " + message);
    }
}