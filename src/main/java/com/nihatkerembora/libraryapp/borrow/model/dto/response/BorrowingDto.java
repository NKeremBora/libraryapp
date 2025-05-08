package com.nihatkerembora.libraryapp.borrow.model.dto.response;
import java.time.LocalDateTime;


public record BorrowingDto(
        String id,
        String bookId,
        String patronId,
        LocalDateTime borrowedAt,
        LocalDateTime dueAt,
        LocalDateTime returnedAt,
        String status
) {}
