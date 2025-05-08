package com.nihatkerembora.libraryapp.borrow.controller;


import com.nihatkerembora.libraryapp.borrow.model.dto.request.BorrowRequest;
import com.nihatkerembora.libraryapp.borrow.model.dto.response.BorrowingDto;
import com.nihatkerembora.libraryapp.borrow.model.enums.BorrowStatus;
import com.nihatkerembora.libraryapp.borrow.service.BorrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/borrowings")
@RequiredArgsConstructor
public class BorrowingController {

    private final BorrowService service;

    @PostMapping
    public ResponseEntity<BorrowingDto> borrowBook(@RequestBody BorrowRequest request) {
        BorrowingDto result = service.borrowBook(request.bookId());
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("/{id}/return")
    public ResponseEntity<BorrowingDto> returnBook(@PathVariable String id) {
        BorrowingDto result = service.returnBook(id);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<Page<BorrowingDto>> getBorrowings(
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
        return ResponseEntity.ok(page);
    }

    @GetMapping("/overdue")
    public ResponseEntity<Page<BorrowingDto>> getOverdue(Pageable pageable) {
        Page<BorrowingDto> page = service.getOverdueBorrowings(pageable);
        return ResponseEntity.ok(page);
    }
}
