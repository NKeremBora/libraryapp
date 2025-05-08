package com.nihatkerembora.libraryapp.auth.exception;

import org.springframework.http.HttpStatus;


/**
 * Exception thrown when a user's status is invalid for the requested operation.
 */
public class UserStatusNotValidException extends RuntimeException {


    public static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;

    private static final String DEFAULT_MESSAGE = """
            User status is not valid!
            """;

    /**
     * Constructs a {@code UserStatusNotValidException} with a default message.
     */
    public UserStatusNotValidException() {
        super(DEFAULT_MESSAGE);
    }

    /**
     * Constructs a {@code UserStatusNotValidException} with additional context.
     *
     * @param message further explanation of the invalid status
     */
    public UserStatusNotValidException(final String message) {
        super(DEFAULT_MESSAGE + " " + message);
    }

}
