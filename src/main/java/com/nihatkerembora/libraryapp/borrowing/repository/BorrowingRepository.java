package com.nihatkerembora.libraryapp.borrowing.repository;


import com.nihatkerembora.libraryapp.borrowing.model.entity.Borrowing;
import com.nihatkerembora.libraryapp.borrowing.model.enums.BorrowStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BorrowingRepository extends JpaRepository<Borrowing, String>, JpaSpecificationExecutor<Borrowing> {

    /**
     * Kullanıcının (patronId) tüm ödünç geçmişi
     */
    List<Borrowing> findByPatronId(String patronId);

    /**
     * Belirli bir statüdeki kayıtlar (örneğin BORROWED ya da RETURNED)
     */
    List<Borrowing> findByStatus(BorrowStatus status);

    /**
     * Süresi dolmuş (dueAt geçmiş ve henüz RETURNED olmayan) kayıtlar
     */
    List<Borrowing> findByDueAtBeforeAndStatus(LocalDateTime dueAt, BorrowStatus status);

    Page<Borrowing> findByDueAtBeforeAndStatus(LocalDateTime due, BorrowStatus status, Pageable pageable);

    @Query("""
    SELECT b FROM Borrowing b
    WHERE 
        (b.status = 'BORROWED' AND b.dueAt < CURRENT_TIMESTAMP)
        OR (b.status = 'RETURNED' AND b.returnedAt > b.dueAt)
""")
    Page<Borrowing> findAllOverdue(Pageable pageable);

}




