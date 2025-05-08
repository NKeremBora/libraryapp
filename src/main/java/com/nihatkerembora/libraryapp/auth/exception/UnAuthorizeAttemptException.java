package com.nihatkerembora.libraryapp.auth.exception;

import org.springframework.http.HttpStatus;


/**
 * Exception thrown when a user attempts an operation without the required authorization.
 */
public class UnAuthorizeAttemptException extends RuntimeException {


    public static final HttpStatus STATUS = HttpStatus.UNAUTHORIZED;

    private static final String DEFAULT_MESSAGE = """
            You do not have permission to create a to-do item.
            """;

    /**
     * Constructs a {@code UnAuthorizeAttemptException} with a predefined message.
     */
    public UnAuthorizeAttemptException() {
        super(DEFAULT_MESSAGE);
    }

}
