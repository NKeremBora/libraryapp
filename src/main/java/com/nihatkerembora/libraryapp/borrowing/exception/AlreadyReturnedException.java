package com.nihatkerembora.libraryapp.borrowing.exception;

import org.springframework.http.HttpStatus;

public class AlreadyReturnedException extends RuntimeException {

    public static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;

    private static final String DEFAULT_MESSAGE = """
            Borrowing is already returned!
            """;

    public AlreadyReturnedException() {
        super(DEFAULT_MESSAGE);
    }

    public AlreadyReturnedException(final String message) {
        super(DEFAULT_MESSAGE + " " + message);
    }
}
