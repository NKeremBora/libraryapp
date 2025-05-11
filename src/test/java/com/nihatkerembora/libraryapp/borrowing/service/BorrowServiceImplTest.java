package com.nihatkerembora.libraryapp.borrowing.service;


import com.nihatkerembora.libraryapp.base.AbstractBaseServiceTest;
import com.nihatkerembora.libraryapp.borrowing.model.dto.response.BorrowingDto;
import com.nihatkerembora.libraryapp.borrowing.model.entity.Borrowing;
import com.nihatkerembora.libraryapp.borrowing.model.enums.BorrowStatus;
import com.nihatkerembora.libraryapp.borrowing.port.out.BookAvailabilityPort;
import com.nihatkerembora.libraryapp.borrowing.port.out.UserStatusPort;
import com.nihatkerembora.libraryapp.borrowing.repository.BorrowingRepository;
import com.nihatkerembora.libraryapp.borrowing.service.impl.BorrowServiceImpl;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.*;

/**
 * BorrowServiceImpl unit-tests (no database, all collaborators mocked)
 */
class BorrowServiceImplTest extends AbstractBaseServiceTest {

    @InjectMocks
    private BorrowServiceImpl service;

    @Mock
    private BorrowingRepository repo;
    @Mock
    private BookAvailabilityPort bookPort;
    @Mock
    private UserStatusPort userPort;

    private String patronId;
    private String bookId;
    private Borrowing borrowing;
    private BorrowingDto dto;

    @BeforeEach
    void setUp() {
        patronId = UUID.randomUUID().toString();
        bookId   = UUID.randomUUID().toString();

        borrowing = Borrowing.builder()
                .id(UUID.randomUUID().toString())
                .bookId(bookId)
                .patronId(patronId)
                .borrowedAt(LocalDateTime.now())
                .dueAt(LocalDateTime.now().plusWeeks(2))
                .status(BorrowStatus.BORROWED)
                .build();

        dto = BorrowingDto.builder()
                .id(borrowing.getId())
                .bookId(bookId)
                .patronId(patronId)
                .status(BorrowStatus.BORROWED.toString())
                .build();

        // SecurityContext hazırlığı
        Jwt jwt = Jwt.withTokenValue("t")
                .claim("userId", patronId)
                .header("alg", "none")
                .build();
        TestingAuthenticationToken auth =
                new TestingAuthenticationToken(jwt, null, "USER");
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    @DisplayName("borrowBook()")
    class BorrowBook {

        @Test
        void userInactive() {
            given(userPort.isActive(patronId)).willReturn(false);

            assertThatThrownBy(() -> service.borrowBook(bookId))
                    .isInstanceOf(IllegalStateException.class);

            then(repo).should(never()).save(any());
            then(bookPort).should(never()).markBorrowed(any());
        }

        @Test
        void bookUnavailable() {
            given(userPort.isActive(patronId)).willReturn(true);
            given(bookPort.isAvailable(bookId)).willReturn(false);

            assertThatThrownBy(() -> service.borrowBook(bookId))
                    .isInstanceOf(IllegalStateException.class);

            then(repo).should(never()).save(any());
        }

        @Test
        void success() {
            given(userPort.isActive(patronId)).willReturn(true);
            given(bookPort.isAvailable(bookId)).willReturn(true);

            // repo.save(...) çağrısını yakala ve id ataması yap
            given(repo.save(any())).willAnswer(inv -> {
                Borrowing b = inv.getArgument(0);
                b.setId(UUID.randomUUID().toString());
                return b;
            });

            BorrowingDto res = service.borrowBook(bookId);

            then(repo).should().save(any(Borrowing.class));
            then(bookPort).should().markBorrowed(bookId);

            assertThat(res.getBookId()).isEqualTo(bookId);
            assertThat(res.getPatronId()).isEqualTo(patronId);
            assertThat(res.getStatus()).isEqualTo(BorrowStatus.BORROWED.toString());
        }
    }

    @Nested
    @DisplayName("returnBook()")
    class ReturnBook {

        @Test
        void notFound() {
            given(repo.findById("no-id")).willReturn(Optional.empty());

            assertThatThrownBy(() -> service.returnBook("no-id"))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        void notOwner() {
            // mevcut kitabı/kiralamayı taklit edecek ama farklı patronId ile yeni bir nesne
            Borrowing other = Borrowing.builder()
                    .id(UUID.randomUUID().toString())   // herhangi bir kimlik
                    .bookId(bookId)                     // aynı kitap
                    .patronId("someone-else")           // farklı kullanıcı
                    .borrowedAt(borrowing.getBorrowedAt())
                    .dueAt(borrowing.getDueAt())
                    .status(BorrowStatus.BORROWED)
                    .build();

            given(repo.findById(other.getId())).willReturn(Optional.of(other));

            assertThatThrownBy(() -> service.returnBook(other.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        void alreadyReturned() {
            // “iade edilmiş” durumunda yeni nesne oluştur
            Borrowing returned = Borrowing.builder()
                    .id(UUID.randomUUID().toString())
                    .bookId(bookId)
                    .patronId(patronId)
                    .borrowedAt(borrowing.getBorrowedAt())
                    .dueAt(borrowing.getDueAt())
                    .returnedAt(LocalDateTime.now())
                    .status(BorrowStatus.RETURNED)
                    .build();

            given(repo.findById(returned.getId())).willReturn(Optional.of(returned));

            assertThatThrownBy(() -> service.returnBook(returned.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        void success() {
            given(repo.findById(borrowing.getId())).willReturn(Optional.of(borrowing));
            given(repo.save(any())).willAnswer(inv -> inv.getArgument(0));

            BorrowingDto res = service.returnBook(borrowing.getId());

            ArgumentCaptor<Borrowing> captor = ArgumentCaptor.forClass(Borrowing.class);
            then(repo).should().save(captor.capture());
            Borrowing saved = captor.getValue();
            assertThat(saved.getStatus()).isEqualTo(BorrowStatus.RETURNED);
            then(bookPort).should().markAvailable(bookId);

            assertThat(res.getStatus()).isEqualTo(BorrowStatus.RETURNED.toString());
        }
    }


    @Test
    void search_respectsCurrentUserWhenNotAdmin() {
        given(repo.findAll(
                any(Specification.class),
                any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of(borrowing)));

        service.searchBorrowings(null, null,
                null, null, null, null, Pageable.unpaged());

        then(repo).should().findAll(
                argThat((Specification<Borrowing> spec) -> spec != null),
                any(Pageable.class));
    }
}
