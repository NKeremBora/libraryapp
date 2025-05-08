package com.nihatkerembora.libraryapp.borrow.repository;


import com.nihatkerembora.libraryapp.borrow.model.entity.Borrowing;
import com.nihatkerembora.libraryapp.borrow.model.enums.BorrowStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

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

    Page<Borrowing> findAll(Pageable pageable);

}




