package com.nihatkerembora.libraryapp.auth.exception;

import org.springframework.http.HttpStatus;

public class UserNotActiveException extends RuntimeException {

    public static final HttpStatus STATUS = HttpStatus.FORBIDDEN;

    private static final String DEFAULT_MESSAGE = """
            User is not active!
            """;

    public UserNotActiveException() {
        super(DEFAULT_MESSAGE);
    }

    public UserNotActiveException(final String message) {
        super(DEFAULT_MESSAGE + " " + message);
    }
}
