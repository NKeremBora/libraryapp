package com.nihatkerembora.libraryapp.borrowing.service.impl;

import com.nihatkerembora.libraryapp.borrowing.model.dto.response.BorrowingDto;
import com.nihatkerembora.libraryapp.borrowing.model.entity.Borrowing;
import com.nihatkerembora.libraryapp.borrowing.model.enums.BorrowStatus;
import com.nihatkerembora.libraryapp.borrowing.model.mapper.BorrowingMapper;
import com.nihatkerembora.libraryapp.borrowing.port.out.BookAvailabilityPort;
import com.nihatkerembora.libraryapp.borrowing.port.out.UserStatusPort;
import com.nihatkerembora.libraryapp.borrowing.repository.BorrowingRepository;
import com.nihatkerembora.libraryapp.borrowing.service.BorrowService;
import com.nihatkerembora.libraryapp.common.utils.PdfUtil;
import com.nihatkerembora.libraryapp.common.utils.ReportColumn;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional
public class BorrowServiceImpl implements BorrowService {

    private final BorrowingRepository borrowingRepository;
    private final BookAvailabilityPort bookAvailabilityPort;
    private final UserStatusPort userStatusPort;

    @Override
    public BorrowingDto borrowBook(String bookId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String patronId = ((Jwt)auth.getPrincipal()).getClaim("userId");

        if (!userStatusPort.isActive(patronId)) {
            throw new IllegalStateException("User is not active");
        }
        if (!bookAvailabilityPort.isAvailable(bookId)) {
            throw new IllegalStateException("Book is not available");
        }

        Borrowing borrowing = Borrowing.builder()
                .bookId(bookId)
                .patronId(patronId)
                .borrowedAt(LocalDateTime.now())
                .dueAt(LocalDateTime.now().plusWeeks(2))
                .status(BorrowStatus.BORROWED)
                .build();
        Borrowing saved = borrowingRepository.save(borrowing);
        bookAvailabilityPort.markBorrowed(bookId);
        return BorrowingMapper.toDto(saved);
    }

    @Override
    public BorrowingDto returnBook(String borrowingId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String patronId = ((Jwt)auth.getPrincipal()).getClaim("userId");

        Borrowing borrowing = borrowingRepository.findById(borrowingId)
                .orElseThrow(() -> new NoSuchElementException("Borrowing not found"));

        if (!borrowing.getPatronId().equals(patronId)) {
            throw new IllegalStateException("Not owner of this borrowing");
        }
        if (borrowing.getStatus() == BorrowStatus.RETURNED) {
            throw new IllegalStateException("Already returned");
        }

        borrowing.setReturnedAt(LocalDateTime.now());
        borrowing.setStatus(BorrowStatus.RETURNED);
        Borrowing saved = borrowingRepository.save(borrowing);
        bookAvailabilityPort.markAvailable(borrowing.getBookId());
        return BorrowingMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BorrowingDto> searchBorrowings(
            String bookId,
            BorrowStatus status,
            LocalDateTime borrowedFrom,
            LocalDateTime borrowedTo,
            LocalDateTime dueFrom,
            LocalDateTime dueTo,
            Pageable pageable
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) auth.getPrincipal();
        String currentUser = jwt.getClaim("userId");
        boolean isAdmin = auth.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"));

        Specification<Borrowing> spec = Specification.where(null);

        if (!isAdmin) {
            spec = spec.and((r, q, cb) -> cb.equal(r.get("patronId"), currentUser));
        }

        if (bookId != null) {
            spec = spec.and((r, q, cb) -> cb.equal(r.get("bookId"), bookId));
        }
        if (status != null) {
            spec = spec.and((r, q, cb) -> cb.equal(r.get("status"), status));
        }
        if (borrowedFrom != null) {
            spec = spec.and((r, q, cb) -> cb.greaterThanOrEqualTo(r.get("borrowedAt"), borrowedFrom));
        }
        if (borrowedTo != null) {
            spec = spec.and((r, q, cb) -> cb.lessThanOrEqualTo(r.get("borrowedAt"), borrowedTo));
        }
        if (dueFrom != null) {
            spec = spec.and((r, q, cb) -> cb.greaterThanOrEqualTo(r.get("dueAt"), dueFrom));
        }
        if (dueTo != null) {
            spec = spec.and((r, q, cb) -> cb.lessThanOrEqualTo(r.get("dueAt"), dueTo));
        }

        return borrowingRepository.findAll(spec, pageable)
                .map(BorrowingMapper::toDto);
    }


    @Override
    @Transactional(readOnly = true)
    public byte[] getOverdueBorrowingsPdf(Pageable pageable){
        Page<BorrowingDto> page = borrowingRepository
                .findAllOverdue(pageable)
                .map(BorrowingMapper::toDto);

        List<BorrowingDto> list = page.getContent();

        List<ReportColumn<BorrowingDto>> columns = List.of(
                new ReportColumn<>("User ID",  BorrowingDto::getPatronId),
                new ReportColumn<>("Book ID",  BorrowingDto::getBookId)
        );

        return PdfUtil.generatePdf("Overdue Borrowings Report", columns, list);
    }
}