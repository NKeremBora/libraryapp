package com.nihatkerembora.libraryapp.borrow.exception;

import org.springframework.http.HttpStatus;

public class BorrowingNotFoundException extends RuntimeException {

    public static final HttpStatus STATUS = HttpStatus.NOT_FOUND;

    private static final String DEFAULT_MESSAGE = """
            Borrowing not found!
            """;

    public BorrowingNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public BorrowingNotFoundException(final String message) {
        super(DEFAULT_MESSAGE + " " + message);
    }
}
