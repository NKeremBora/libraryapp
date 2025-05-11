package com.nihatkerembora.libraryapp.borrowing.controller;


import com.nihatkerembora.libraryapp.borrowing.model.dto.request.BorrowRequest;
import com.nihatkerembora.libraryapp.borrowing.model.dto.response.BorrowingDto;
import com.nihatkerembora.libraryapp.borrowing.model.enums.BorrowStatus;
import com.nihatkerembora.libraryapp.borrowing.service.BorrowService;
import com.nihatkerembora.libraryapp.common.model.dto.response.CustomPagingResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * REST controller for managing borrow operations.
 * <p>
 * Provides endpoints to borrow and return books, search/filter borrow records,
 * and download a PDF report of overdue borrowings.
 * </p>
 */
@RestController
@RequestMapping("/api/v1/borrowings")
@RequiredArgsConstructor
public class BorrowingController {

    private final BorrowService service;


    /**
     * Creates a new borrowing record for the given book.
     *
     * @param request the borrow request containing the ID of the book to borrow
     * @return the created {@link BorrowingDto}, with HTTP status 201
     */
    @Operation(
            summary = "Borrow a book",
            description = "Creates a new borrowing record for the specified book.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Book successfully borrowed",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = BorrowingDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid request data"),
                    @ApiResponse(responseCode = "404", description = "Book not found or not available")
            }
    )
    @PostMapping
    public ResponseEntity<BorrowingDto> borrowBook(@RequestBody BorrowRequest request) {
        BorrowingDto result = service.borrowBook(request.bookId());
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * Returns a previously borrowed book.
     *
     * @param id the ID of the borrowing record to return
     * @return the updated {@link BorrowingDto}
     */
    @Operation(
            summary = "Return a book",
            description = "Marks the borrowing record as returned for the given ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Book successfully returned",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = BorrowingDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Borrowing record not found")
            }
    )
    @PutMapping("/{id}/return")
    public ResponseEntity<BorrowingDto> returnBook(@PathVariable String id) {
        BorrowingDto result = service.returnBook(id);
        return ResponseEntity.ok(result);
    }


    /**
     * Searches and filters borrowing records.
     *
     * @param bookId      optional book ID to filter by
     * @param status      optional borrowing status to filter by
     * @param borrowedFrom optional lower bound for borrow timestamp (inclusive)
     * @param borrowedTo   optional upper bound for borrow timestamp (inclusive)
     * @param dueFrom      optional lower bound for due timestamp (inclusive)
     * @param dueTo        optional upper bound for due timestamp (inclusive)
     * @param pageable     pagination and sorting parameters
     * @return a paginated {@link CustomPagingResponse} of {@link BorrowingDto}
     */
    @Operation(
            summary = "List borrowings",
            description = "Retrieves a pageable list of borrowing records with optional filters.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Borrowing records retrieved",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = CustomPagingResponse.class)
                            )
                    )
            }
    )
    @GetMapping
    public ResponseEntity<CustomPagingResponse<BorrowingDto>> getBorrowings(
            @RequestParam(required = false) String bookId,
            @RequestParam(required = false) BorrowStatus status,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime borrowedFrom,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime borrowedTo,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dueFrom,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dueTo,
            Pageable pageable
    ) {
        Page<BorrowingDto> page = service.searchBorrowings(
                bookId, status,
                borrowedFrom, borrowedTo,
                dueFrom, dueTo,
                pageable
        );
        CustomPagingResponse<BorrowingDto> response = CustomPagingResponse.from(page);
        return ResponseEntity.ok(response);
    }

    /**
     * Generates and downloads a PDF report of overdue borrowings.
     *
     * @param pageable paging for the list used in the report
     * @return a PDF file as {@code application/pdf} attachment
     * @throws IOException if PDF generation fails
     */
    @Operation(
            summary = "Download overdue borrowings PDF",
            description = "Generates a PDF report of all overdue borrowings and returns it as a downloadable file.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "PDF report generated",
                            content = @Content(mediaType = MediaType.APPLICATION_PDF_VALUE)
                    ),
                    @ApiResponse(responseCode = "500", description = "Error generating PDF")
            }
    )
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/overdue")
    public ResponseEntity<byte[]> getOverduePdf(Pageable pageable) throws IOException {
        byte[] pdf = service.getOverdueBorrowingsPdf(pageable);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=overdue_borrowings.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
