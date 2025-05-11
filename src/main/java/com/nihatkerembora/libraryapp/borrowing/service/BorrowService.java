package com.nihatkerembora.libraryapp.borrowing.service;

import com.nihatkerembora.libraryapp.borrowing.model.dto.response.BorrowingDto;
import com.nihatkerembora.libraryapp.borrowing.model.enums.BorrowStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface BorrowService {
    /**
     * JWT içinden patronId alarak ödünç alma işlemi yapar.
     */
    BorrowingDto borrowBook(String bookId);

    /**
     * JWT içinden patronId alarak iade işlemi yapar.
     */
    BorrowingDto returnBook(String borrowingId);

    /**
     * Dinamik filtreli arama.
     * - NORMAL USER yalnızca kendi kayıtlarını,
     * - ADMIN ise tümünü (veya ek patronId filtresi var ise onu) görür.
     */
    Page<BorrowingDto> searchBorrowings(
            // ADMIN için opsiyonel
            String bookId,
            BorrowStatus status,
            LocalDateTime borrowedFrom,
            LocalDateTime borrowedTo,
            LocalDateTime dueFrom,
            LocalDateTime dueTo,
            Pageable pageable
    );

    byte[]  getOverdueBorrowingsPdf(Pageable pageable);
}
