package com.nihatkerembora.libraryapp.borrowing.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BorrowingDto {

    private String id;
    private String bookId;
    private String patronId;
    private LocalDateTime borrowedAt;
    private LocalDateTime dueAt;
    private LocalDateTime returnedAt;
    private String status;
}
