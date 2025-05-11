package com.nihatkerembora.libraryapp.borrowing.config;

import com.nihatkerembora.libraryapp.auth.model.entity.UserEntity;
import com.nihatkerembora.libraryapp.auth.repository.UserRepository;
import com.nihatkerembora.libraryapp.book.model.entity.Book;
import com.nihatkerembora.libraryapp.book.repository.BookRepository;
import com.nihatkerembora.libraryapp.borrowing.model.entity.Borrowing;
import com.nihatkerembora.libraryapp.borrowing.model.enums.BorrowStatus;
import com.nihatkerembora.libraryapp.borrowing.repository.BorrowingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Order(3)
@RequiredArgsConstructor
@Slf4j
public class BorrowingDataInitializer implements CommandLineRunner {

    private final BorrowingRepository borrowingRepo;
    private final BookRepository bookRepo;
    private final UserRepository userRepo;

    @Override
    @Transactional
    public void run(String... args) {
        if (borrowingRepo.count() > 0) {
            return;
        }

        List<Book> books = bookRepo.findAll();
        List<UserEntity> users = userRepo.findAll();

        if (books.size() < 3 || users.size() < 2) {
            log.warn("Not enough data to initialize borrowings (books={}, users={})", books.size(), users.size());
            return;
        }

        String book1Id = books.get(0).getId().toString();
        String book2Id = books.get(1).getId().toString();
        String book3Id = books.get(2).getId().toString();

        String user1Id = users.get(0).getId();
        String user2Id = users.get(1).getId();

        LocalDateTime now = LocalDateTime.now();

        List<Borrowing> initialBorrowings = List.of(
                Borrowing.builder()
                        .bookId(book1Id)
                        .patronId(user1Id)
                        .borrowedAt(now.minusDays(1))
                        .dueAt(now.plusDays(6))
                        .status(BorrowStatus.BORROWED)
                        .build(),

                Borrowing.builder()
                        .bookId(book3Id)
                        .patronId(user1Id)
                        .borrowedAt(now.minusWeeks(3))
                        .dueAt(now.minusDays(1))
                        .status(BorrowStatus.BORROWED)
                        .build(),

                Borrowing.builder()
                        .bookId(book3Id)
                        .patronId(user2Id)
                        .borrowedAt(now.minusDays(10))
                        .dueAt(now.minusDays(3))
                        .returnedAt(now.minusDays(2))
                        .status(BorrowStatus.RETURNED)
                        .build(),

                Borrowing.builder()
                        .bookId(book2Id)
                        .patronId(user1Id)
                        .borrowedAt(now.minusDays(10))
                        .dueAt(now.minusDays(5))
                        .returnedAt(now.minusDays(2))
                        .status(BorrowStatus.RETURNED)
                        .build(),

                Borrowing.builder()
                        .bookId(book1Id)
                        .patronId(user2Id)
                        .borrowedAt(now.minusDays(12))
                        .dueAt(now.minusDays(6))
                        .returnedAt(now.minusDays(4))
                        .status(BorrowStatus.RETURNED)
                        .build()
        );

        borrowingRepo.saveAll(initialBorrowings);
        log.info(">>>>> Initial borrowings loaded: {}", initialBorrowings.size());
    }
}
