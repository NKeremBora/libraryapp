package com.nihatkerembora.libraryapp.borrow.service.impl;

import com.nihatkerembora.libraryapp.auth.security.AuthenticationFacade;
import com.nihatkerembora.libraryapp.borrow.model.dto.response.BorrowingDto;
import com.nihatkerembora.libraryapp.borrow.model.entity.Borrowing;
import com.nihatkerembora.libraryapp.borrow.model.enums.BorrowStatus;
import com.nihatkerembora.libraryapp.borrow.model.mapper.BorrowingMapper;
import com.nihatkerembora.libraryapp.borrow.port.out.BookAvailabilityPort;
import com.nihatkerembora.libraryapp.borrow.port.out.UserStatusPort;
import com.nihatkerembora.libraryapp.borrow.repository.BorrowingRepository;
import com.nihatkerembora.libraryapp.borrow.service.BorrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BorrowServiceImpl implements BorrowService {

    private final BorrowingRepository borrowingRepository;
    private final BookAvailabilityPort bookAvailabilityPort;
    private final UserStatusPort userStatusPort;
    private final AuthenticationFacade authFacade;

    @Override
    public BorrowingDto borrowBook(String bookId) {
        Authentication auth = authFacade.getAuthentication();
        String patronId = ((Jwt)auth.getPrincipal()).getClaim("user_id");

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
        Authentication auth = authFacade.getAuthentication();
        String patronId = ((Jwt)auth.getPrincipal()).getClaim("user_id");

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
        Authentication auth = authFacade.getAuthentication();
        Jwt jwt = (Jwt) auth.getPrincipal();
        String currentUser = jwt.getClaim("user_id");
        boolean isAdmin = auth.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

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
    public Page<BorrowingDto> getOverdueBorrowings(Pageable pageable) {
        return borrowingRepository
                .findByDueAtBeforeAndStatus(LocalDateTime.now(), BorrowStatus.BORROWED, pageable)
                .map(BorrowingMapper::toDto);
    }
}