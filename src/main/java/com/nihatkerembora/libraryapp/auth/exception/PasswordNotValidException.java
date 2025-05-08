package com.nihatkerembora.libraryapp.auth.exception;


import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a provided password is not valid during authentication or validation.
 */
public class PasswordNotValidException extends RuntimeException {


    public static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;

    private static final String DEFAULT_MESSAGE = """
            Password is not valid!
            """;

    /**
     * Constructs a {@code PasswordNotValidException} with a default message.
     */
    public PasswordNotValidException() {
        super(DEFAULT_MESSAGE);
    }

    /**
     * Constructs a {@code PasswordNotValidException} with additional context message.
     *
     * @param message additional information about the password failure
     */
    public PasswordNotValidException(final String message) {
        super(DEFAULT_MESSAGE + " " + message);
    }

}