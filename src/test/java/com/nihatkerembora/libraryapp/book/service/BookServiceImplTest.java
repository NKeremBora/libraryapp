package com.nihatkerembora.libraryapp.book.service;

import com.nihatkerembora.libraryapp.base.AbstractBaseServiceTest;
import com.nihatkerembora.libraryapp.book.exception.BookAlreadyExistException;
import com.nihatkerembora.libraryapp.book.exception.BookNotFoundException;
import com.nihatkerembora.libraryapp.book.exception.GenreNotFoundException;
import com.nihatkerembora.libraryapp.book.exception.IsbnAlreadyExistsException;
import com.nihatkerembora.libraryapp.book.model.dto.event.AvailabilityEvent;
import com.nihatkerembora.libraryapp.book.model.dto.request.BookCreateRequest;
import com.nihatkerembora.libraryapp.book.model.dto.request.BookUpdateRequest;
import com.nihatkerembora.libraryapp.book.model.dto.response.BookResponse;
import com.nihatkerembora.libraryapp.book.model.entity.Book;
import com.nihatkerembora.libraryapp.book.model.entity.Genre;
import com.nihatkerembora.libraryapp.book.model.enums.Status;
import com.nihatkerembora.libraryapp.book.model.mapper.BookMapper;
import com.nihatkerembora.libraryapp.book.publisher.AvailabilityPublisher;
import com.nihatkerembora.libraryapp.book.repository.BookRepository;
import com.nihatkerembora.libraryapp.book.repository.GenreRepository;
import com.nihatkerembora.libraryapp.book.service.impl.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

/**
 * BookServiceImpl unit-tests (no database, all collaborators mocked)
 */

class BookServiceImplTest extends AbstractBaseServiceTest {

    @InjectMocks
    private BookServiceImpl service;

    @Mock
    private BookRepository bookRepo;
    @Mock
    private GenreRepository genreRepo;
    @Mock
    private BookMapper mapper;
    @Mock
    private AvailabilityPublisher publisher;

    private BookCreateRequest createReq;
    private Book entity;
    private BookResponse dto;
    private String genreId;
    private Genre genre;

    @BeforeEach
    void init() {
        genreId = UUID.randomUUID().toString();
        genre = Genre.builder().id(genreId).name("Fiction").build();

        createReq = BookCreateRequest.builder()
                .title("Clean Code")
                .author("Robert C. Martin")
                .isbn("9780132350884")
                .publicationDate(LocalDate.of(2008, 8, 1))
                .genreIds(List.of())
                .build();

        entity = Book.builder()
                .id(UUID.randomUUID().toString())
                .title(createReq.getTitle())
                .author(createReq.getAuthor())
                .isbn(createReq.getIsbn())
                .genres(Set.of(genre))
                .status(Status.AVAILABLE)
                .build();

        dto = BookResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .author(entity.getAuthor())
                .isbn(entity.getIsbn())
                .build();
    }


    @Test
    @DisplayName("add() → ISBN zaten varsa BookAlreadyExistException fırlatır")
    void add_existingIsbn() {
        given(bookRepo.existsByIsbnAndStatusNot(createReq.getIsbn(), Status.DELETED)).willReturn(true);

        assertThatThrownBy(() -> service.add(createReq))
                .isInstanceOf(BookAlreadyExistException.class);

        then(bookRepo).should(never()).save(any());
    }


    @Test
    void get_success() {
        given(bookRepo.findByIdAndStatusNot(entity.getId().toString(), Status.DELETED))
                .willReturn(Optional.of(entity));
        given(mapper.toDto(entity)).willReturn(dto);

        BookResponse res = service.get(entity.getId().toString());

        assertThat(res).isEqualTo(dto);
    }

    @Test
    void get_notFound() {
        given(bookRepo.findByIdAndStatusNot(anyString(), any())).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.get("no-id"))
                .isInstanceOf(BookNotFoundException.class);
    }

    @Test
    void update_isbnConflict() {
        BookUpdateRequest upd = BookUpdateRequest.builder()
                .title("x")
                .author("y")
                .isbn("new-isbn")
                .genreIds(List.of(genreId))
                .build();

        given(bookRepo.findByIdAndStatusNot(entity.getId().toString(), Status.DELETED))
                .willReturn(Optional.of(entity));
        given(bookRepo.existsByIsbnAndStatusNot("new-isbn", Status.DELETED))
                .willReturn(true);

        assertThatThrownBy(() -> service.update(entity.getId().toString(), upd))
                .isInstanceOf(IsbnAlreadyExistsException.class);
    }

    @Test
    void update_missingGenre() {
        BookUpdateRequest upd = BookUpdateRequest.builder()
                .title("x")
                .author("y")
                .isbn(entity.getIsbn())
                .genreIds(List.of(genreId))
                .build();

        given(bookRepo.findByIdAndStatusNot(entity.getId().toString(), Status.DELETED))
                .willReturn(Optional.of(entity));
        given(bookRepo.existsByIsbnAndStatusNot(anyString(), any())).willReturn(false);
        given(genreRepo.findAllById(upd.getGenreIds())).willReturn(List.of()); // boş

        assertThatThrownBy(() -> service.update(entity.getId().toString(), upd))
                .isInstanceOf(GenreNotFoundException.class);
    }

    @Test
    void delete_marksStatusDeleted() {
        given(bookRepo.findByIdAndStatusNot(entity.getId().toString(), Status.DELETED))
                .willReturn(Optional.of(entity));

        service.delete(entity.getId().toString());

        then(bookRepo).should().save(argThat(b -> b.getStatus() == Status.DELETED));
    }


    @Nested
    class AvailabilityOps {

        @Test
        void markBorrowed_updatesStatusAndPublishesEvent() {
            given(bookRepo.findById(entity.getId().toString())).willReturn(Optional.of(entity));
            // void method -> doNothing default

            boolean res = service.markBorrowed(entity.getId().toString());

            assertThat(res).isTrue();
            then(bookRepo).should().updateStatus(entity.getId().toString(), Status.BORROWED);
            then(publisher).should()
                    .publish(new AvailabilityEvent(entity.getId().toString(), entity.getTitle(), Status.BORROWED));
        }

        @Test
        void markAvailable_updatesStatusAndPublishesEvent() {
            given(bookRepo.findById(entity.getId().toString())).willReturn(Optional.of(entity));

            service.markAvailable(entity.getId().toString());

            then(bookRepo).should().updateStatus(entity.getId().toString(), Status.AVAILABLE);
            then(publisher).should()
                    .publish(new AvailabilityEvent(entity.getId().toString(), entity.getTitle(), Status.AVAILABLE));
        }
    }

    @Test
    void isAvailable_delegatesToRepo() {
        given(bookRepo.existsByIdAndStatus(entity.getId().toString(), Status.AVAILABLE))
                .willReturn(true);

        assertThat(service.isAvailable(entity.getId().toString())).isTrue();
    }
}