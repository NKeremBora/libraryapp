package com.nihatkerembora.libraryapp.auth.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when attempting to register a user that already exists.
 */
public class UserAlreadyExistException extends RuntimeException {

    public static final HttpStatus STATUS = HttpStatus.CONFLICT;

    private static final String DEFAULT_MESSAGE = """
            User already exist!
            """;

    /**
     * Constructs a {@code UserAlreadyExistException} with a default message.
     */
    public UserAlreadyExistException() {
        super(DEFAULT_MESSAGE);
    }

    /**
     * Constructs a {@code UserAlreadyExistException} with additional context.
     *
     * @param message additional detail about the conflict
     */
    public UserAlreadyExistException(final String message) {
        super(DEFAULT_MESSAGE + " " + message);
    }

}
