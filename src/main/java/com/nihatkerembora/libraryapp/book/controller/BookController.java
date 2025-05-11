package com.nihatkerembora.libraryapp.book.controller;


import com.nihatkerembora.libraryapp.book.model.dto.request.BookCreateRequest;
import com.nihatkerembora.libraryapp.book.model.dto.request.BookUpdateRequest;
import com.nihatkerembora.libraryapp.book.model.dto.response.BookResponse;
import com.nihatkerembora.libraryapp.book.service.BookService;
import com.nihatkerembora.libraryapp.common.model.dto.response.CustomPagingResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping(value = "/api/v1/books",
        produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Validated
public class BookController {

    private final BookService bookService;


    /**
     * Endpoint to create a new book.
     *
     * @param request The {@link BookCreateRequest} containing book details to be created.
     * @return The {@link BookResponse} representing the created book.
     */
    @Operation(
            summary = "Add a new book",
            description = "Adds a new book to the system with the given title, author, ISBN and genres.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Book successfully created"),
                    @ApiResponse(responseCode = "400", description = "Invalid request data"),
                    @ApiResponse(responseCode = "409", description = "Book with same ISBN already exists")
            }
    )
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<BookResponse> addBook(
            @Valid @RequestBody BookCreateRequest request) {

        BookResponse created = bookService.add(request);
        URI location = URI.create("/api/v1/books/" + created.getId());

        return ResponseEntity
                .created(location)
                .body(created);
    }

    /**
     * Endpoint to get a book by its ID.
     *
     * @param id The ID of the book.
     * @return The {@link BookResponse} representing the fetched book.
     */
    @Operation(
            summary = "Get book by ID",
            description = "Retrieves details of a book by its unique ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Book found"),
                    @ApiResponse(responseCode = "404", description = "Book not found")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBook(@PathVariable String id) {
        BookResponse book = bookService.get(id);
        return ResponseEntity.ok(book);
    }

    /**
     * Endpoint to search books with filters.
     *
     * @param title   (Optional) Title of the book.
     * @param author  (Optional) Author of the book.
     * @param isbn    (Optional) ISBN of the book.
     * @param genre   (Optional) Genre name to filter books.
     * @param pageable Pagination and sorting information.
     * @return A paginated {@link ResponseEntity<CustomPagingResponse<BookResponse>>} containing search results.
     */
    @Operation(
            summary = "Search books",
            description = "Searches books with optional filters like title, author, ISBN and genre name.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Books found matching the search criteria")
            }
    )
    @GetMapping
    public ResponseEntity<CustomPagingResponse<BookResponse>> search(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String isbn,
            @RequestParam(required = false) String genre,
            @PageableDefault(size = 20, sort = "title") Pageable pageable) {

        Page<BookResponse> page = bookService.search(title, author, isbn, genre, pageable);
        CustomPagingResponse<BookResponse> response = CustomPagingResponse.from(page);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint to update an existing book.
     *
     * @param id The ID of the book to update.
     * @param request The {@link BookUpdateRequest} containing new book details.
     * @return The updated {@link BookResponse}.
     */
    @Operation(
            summary = "Update a book",
            description = "Updates an existing book's information by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Book successfully updated"),
                    @ApiResponse(responseCode = "404", description = "Book not found"),
                    @ApiResponse(responseCode = "409", description = "Book with same ISBN already exists")
            }
    )
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> update(
            @PathVariable String id,
            @Valid @RequestBody BookUpdateRequest request) {

        BookResponse updated = bookService.update(id, request);
        return ResponseEntity.ok(updated);
    }

    /**
     * Endpoint to soft delete a book.
     *
     * @param id The ID of the book to delete.
     * @return No content response.
     */
    @Operation(
            summary = "Soft delete a book",
            description = "Marks a book as deleted instead of physically removing it from the database.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Book successfully soft deleted"),
                    @ApiResponse(responseCode = "404", description = "Book not found")
            }
    )
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PutMapping("/{id}/soft-delete")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }
}