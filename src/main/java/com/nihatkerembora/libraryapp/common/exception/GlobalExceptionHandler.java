package com.nihatkerembora.libraryapp.common.exception;

import com.nihatkerembora.libraryapp.auth.exception.*;
import com.nihatkerembora.libraryapp.book.exception.BookNotFoundException;
import com.nihatkerembora.libraryapp.book.exception.GenreInUseException;
import com.nihatkerembora.libraryapp.book.exception.GenreNotFoundException;
import com.nihatkerembora.libraryapp.book.exception.IsbnAlreadyExistsException;
import com.nihatkerembora.libraryapp.borrowing.exception.AlreadyReturnedException;
import com.nihatkerembora.libraryapp.borrowing.exception.BookNotAvailableException;
import com.nihatkerembora.libraryapp.borrowing.exception.BorrowingNotFoundException;
import com.nihatkerembora.libraryapp.common.model.CustomError;
import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * Global exception handler for the Car Service application.
 * This class handles various exceptions thrown throughout the application and returns
 * standardized {@link CustomError} responses with appropriate HTTP status codes.
 *
 * <p>Handled exceptions include:</p>
 * <ul>
 *     <li>Validation errors</li>
 *     <li>Authentication and authorization issues</li>
 *     <li>Resource not found and conflict scenarios</li>
 *     <li>Domain-specific application exceptions</li>
 * </ul>
 *
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex) {
        List<CustomError.CustomSubError> subErrors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            subErrors.add(CustomError.CustomSubError.builder()
                    .field(field)
                    .message(message)
                    .build());
        });
        CustomError error = CustomError.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .header(CustomError.Header.VALIDATION_ERROR.getName())
                .message("Validation failed")
                .subErrors(subErrors)
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<Object> handleConstraintViolation(final ConstraintViolationException ex) {
        List<CustomError.CustomSubError> subErrors = new ArrayList<>();
        ex.getConstraintViolations().forEach(cv -> {
            subErrors.add(CustomError.CustomSubError.builder()
                    .field(StringUtils.substringAfterLast(cv.getPropertyPath().toString(), "."))
                    .message(cv.getMessage())
                    .value(cv.getInvalidValue() != null ? cv.getInvalidValue().toString() : null)
                    .type(cv.getInvalidValue() != null ? cv.getInvalidValue().getClass().getSimpleName() : null)
                    .build());
        });
        CustomError error = CustomError.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .header(CustomError.Header.VALIDATION_ERROR.getName())
                .message("Constraint violation")
                .subErrors(subErrors)
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<?> handleAccessDenied(final AccessDeniedException ex) {
        CustomError error = CustomError.builder()
                .httpStatus(HttpStatus.FORBIDDEN)
                .header(CustomError.Header.AUTH_ERROR.getName())
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UserNotActiveException.class)
    protected ResponseEntity<CustomError> handleUserNotActive(final UserNotActiveException ex) {
        CustomError error = CustomError.builder()
                .httpStatus(HttpStatus.FORBIDDEN)
                .header(CustomError.Header.AUTH_ERROR.getName())
                .message(ex.getMessage())
                .isSuccess(false)
                .build();
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(BookNotAvailableException.class)
    protected ResponseEntity<CustomError> handleBookNotAvailable(final BookNotAvailableException ex) {
        CustomError error = CustomError.builder()
                .httpStatus(BookNotAvailableException.STATUS)
                .header(CustomError.Header.VALIDATION_ERROR.getName())
                .message(ex.getMessage())
                .isSuccess(false)
                .build();
        return new ResponseEntity<>(error, BookNotAvailableException.STATUS);
    }

    @ExceptionHandler(BorrowingNotFoundException.class)
    protected ResponseEntity<CustomError> handleBorrowingNotFound(final BorrowingNotFoundException ex) {
        CustomError error = CustomError.builder()
                .httpStatus(BorrowingNotFoundException.STATUS)
                .header(CustomError.Header.NOT_FOUND.getName())
                .message(ex.getMessage())
                .isSuccess(false)
                .build();
        return new ResponseEntity<>(error, BorrowingNotFoundException.STATUS);
    }

    @ExceptionHandler(AlreadyReturnedException.class)
    protected ResponseEntity<CustomError> handleAlreadyReturned(final AlreadyReturnedException ex) {
        CustomError error = CustomError.builder()
                .httpStatus(AlreadyReturnedException.STATUS)
                .header(CustomError.Header.VALIDATION_ERROR.getName())
                .message(ex.getMessage())
                .isSuccess(false)
                .build();
        return new ResponseEntity<>(error, AlreadyReturnedException.STATUS);
    }

    @ExceptionHandler(UserNotFoundException.class)
    protected ResponseEntity<CustomError> handleUserNotFound(final UserNotFoundException ex) {
        CustomError error = CustomError.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .header(CustomError.Header.NOT_FOUND.getName())
                .message(ex.getMessage())
                .isSuccess(false)
                .build();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PasswordNotValidException.class)
    protected ResponseEntity<CustomError> handlePasswordNotValid(final PasswordNotValidException ex) {
        CustomError error = CustomError.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .header(CustomError.Header.VALIDATION_ERROR.getName())
                .message(ex.getMessage())
                .isSuccess(false)
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TokenAlreadyInvalidatedException.class)
    protected ResponseEntity<CustomError> handleTokenAlreadyInvalidated(final TokenAlreadyInvalidatedException ex) {
        CustomError error = CustomError.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .header(CustomError.Header.VALIDATION_ERROR.getName())
                .message(ex.getMessage())
                .isSuccess(false)
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    protected ResponseEntity<CustomError> handleUserAlreadyExist(final UserAlreadyExistException ex) {
        CustomError error = CustomError.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .header(CustomError.Header.ALREADY_EXIST.getName())
                .message(ex.getMessage())
                .isSuccess(false)
                .build();
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserStatusNotValidException.class)
    protected ResponseEntity<CustomError> handleUserStatusNotValid(final UserStatusNotValidException ex) {
        CustomError error = CustomError.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .header(CustomError.Header.VALIDATION_ERROR.getName())
                .message(ex.getMessage())
                .isSuccess(false)
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnAuthorizeAttemptException.class)
    protected ResponseEntity<Object> handleUnAuthorizedAttempt(final UnAuthorizeAttemptException ex) {
        CustomError error = CustomError.builder()
                .httpStatus(HttpStatus.UNAUTHORIZED)
                .header(CustomError.Header.AUTH_ERROR.getName())
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    protected ResponseEntity<CustomError> handleNoHandlerFound(final NoHandlerFoundException ex) {
        CustomError error = CustomError.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .header(CustomError.Header.API_ERROR.getName())
                .message("No endpoint " + ex.getRequestURL())
                .isSuccess(false)
                .build();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<CustomError> handleGenericRuntime(final RuntimeException ex) {
        CustomError error = CustomError.builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .header(CustomError.Header.API_ERROR.getName())
                .message(ex.getMessage())
                .isSuccess(false)
                .build();
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(BookNotFoundException.class)
    protected ResponseEntity<CustomError> handleBookNotFound(final BookNotFoundException ex) {
        CustomError error = CustomError.builder()
                .httpStatus(BookNotFoundException.STATUS)
                .header(CustomError.Header.NOT_FOUND.getName())
                .message(ex.getMessage())
                .isSuccess(false)
                .build();
        return new ResponseEntity<>(error, BookNotFoundException.STATUS);
    }

    @ExceptionHandler(IsbnAlreadyExistsException.class)
    protected ResponseEntity<CustomError> handleIsbnAlreadyExists(final IsbnAlreadyExistsException ex) {
        CustomError error = CustomError.builder()
                .httpStatus(IsbnAlreadyExistsException.STATUS)
                .header(CustomError.Header.ALREADY_EXIST.getName())
                .message(ex.getMessage())
                .isSuccess(false)
                .build();
        return new ResponseEntity<>(error, IsbnAlreadyExistsException.STATUS);
    }

    @ExceptionHandler(GenreNotFoundException.class)
    protected ResponseEntity<CustomError> handleGenreNotFound(final GenreNotFoundException ex) {
        CustomError error = CustomError.builder()
                .httpStatus(GenreNotFoundException.STATUS)
                .header(CustomError.Header.NOT_FOUND.getName())
                .message(ex.getMessage())
                .isSuccess(false)
                .build();
        return new ResponseEntity<>(error, GenreNotFoundException.STATUS);
    }

    @ExceptionHandler(GenreInUseException.class)
    protected ResponseEntity<CustomError> handleGenreInUse(final GenreInUseException ex) {
        CustomError error = CustomError.builder()
                .httpStatus(GenreInUseException.STATUS)
                .header(CustomError.Header.VALIDATION_ERROR.getName())
                .message(ex.getMessage())
                .isSuccess(false)
                .build();
        return new ResponseEntity<>(error, GenreInUseException.STATUS);
    }


}