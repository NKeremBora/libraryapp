package com.nihatkerembora.libraryapp.borrow.model.mapper;


import com.nihatkerembora.libraryapp.borrow.model.dto.response.BorrowingDto;
import com.nihatkerembora.libraryapp.borrow.model.entity.Borrowing;

public class BorrowingMapper {
    public static BorrowingDto toDto(Borrowing b) {
        return new BorrowingDto(
                b.getId(),
                b.getBookId(),
                b.getPatronId(),
                b.getBorrowedAt(),
                b.getDueAt(),
                b.getReturnedAt(),
                b.getStatus().name()
        );
    }
}
