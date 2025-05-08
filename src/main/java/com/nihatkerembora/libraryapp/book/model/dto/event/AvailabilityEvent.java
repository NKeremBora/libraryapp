package com.nihatkerembora.libraryapp.book.model.dto.event;


import com.nihatkerembora.libraryapp.book.model.enums.Status;

public record AvailabilityEvent(
        String bookId,
        String title,
        Status status
) {}
