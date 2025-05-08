package com.nihatkerembora.libraryapp.book.service.impl;


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
import com.nihatkerembora.libraryapp.book.reactive.AvailabilityPublisher;
import com.nihatkerembora.libraryapp.book.repository.BookRepository;
import com.nihatkerembora.libraryapp.book.repository.GenreRepository;
import com.nihatkerembora.libraryapp.book.service.BookService;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional  // default: Propagation.REQUIRED
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepo;
    private final GenreRepository genreRepo;
    private final BookMapper mapper;
    private final AvailabilityPublisher publisher;

    @Override
    public BookResponse add(BookCreateRequest req) {
        // ISBN unique validation
        if (bookRepo.existsByIsbnAndStatusNot(req.getIsbn(), Status.DELETED)) {
            throw new BookAlreadyExistException("ISBN already registered");
        }
        // Bulk fetch genres by IDs (single query)
        List<Genre> genresList = genreRepo.findAllById(req.getGenreIds());
        if (genresList.size() != req.getGenreIds().size()) {
            Set<UUID> foundIds = genresList.stream()
                    .map(Genre::getId)
                    .collect(Collectors.toSet());
            UUID missing = req.getGenreIds().stream()
                    .filter(id -> !foundIds.contains(id))
                    .findFirst()
                    .orElseThrow();
            throw new GenreNotFoundException(missing);
        }
        Set<Genre> genres = new HashSet<>(genresList);

        // Map to entity and save
        Book book = mapper.toEntity(req, genres);
        Book saved = bookRepo.save(book);
        return mapper.toDto(saved);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public BookResponse get(String id) {
        Book book = bookRepo.findByIdAndStatusNot(id, Status.DELETED)
                .orElseThrow(() -> new BookNotFoundException(id));
        return mapper.toDto(book);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Page<BookResponse> search(String title,
                                     String author,
                                     String isbn,
                                     String genreName,
                                     Pageable pageable) {
        Specification<Book> spec = (root, query, cb) ->
                cb.notEqual(root.get("status"), Status.DELETED);

        if (StringUtils.hasText(title)) {
            spec = spec.and((r, q, c) ->
                    c.like(c.lower(r.get("title")), "%" + title.toLowerCase() + "%"));
        }

        if (StringUtils.hasText(author)) {
            spec = spec.and((r, q, c) ->
                    c.like(c.lower(r.get("author")), "%" + author.toLowerCase() + "%"));
        }

        if (StringUtils.hasText(isbn)) {
            spec = spec.and((r, q, c) -> c.equal(r.get("isbn"), isbn));
        }

        if (StringUtils.hasText(genreName)) {
            spec = spec.and((r, q, c) -> {
                Join<Book, Genre> join = r.join("genres", JoinType.INNER);
                return c.like(c.lower(join.get("name")), "%" + genreName.toLowerCase() + "%");
            });
        }

        return bookRepo.findAll(spec, pageable)
                .map(mapper::toDto);
    }

    @Override
    public BookResponse update(String id, BookUpdateRequest req) {
        Book existing = bookRepo.findByIdAndStatusNot(id, Status.DELETED)
                .orElseThrow(() -> new BookNotFoundException(id));
        // ISBN conflict check
        if (!existing.getIsbn().equals(req.getIsbn()) &&
                bookRepo.existsByIsbnAndStatusNot(req.getIsbn(), Status.DELETED)) {
            throw new IsbnAlreadyExistsException("ISBN already registered");
        }
        // Bulk fetch new genres
        List<Genre> genresList = genreRepo.findAllById(req.getGenreIds());
        if (genresList.size() != req.getGenreIds().size()) {
            Set<UUID> foundIds = genresList.stream()
                    .map(Genre::getId)
                    .collect(Collectors.toSet());
            UUID missing = req.getGenreIds().stream()
                    .filter(gid -> !foundIds.contains(gid))
                    .findFirst()
                    .orElseThrow();
            throw new GenreNotFoundException(missing);
        }
        Set<Genre> genres = new HashSet<>(genresList);

        Book updatedEntity = mapper.toEntity(req, genres);
        updatedEntity.setId(existing.getId());
        updatedEntity.setStatus(existing.getStatus());

        Book saved = bookRepo.save(updatedEntity);
        return mapper.toDto(saved);
    }

    @Override
    public void delete(String id) {
        Book book = bookRepo.findByIdAndStatusNot(id, Status.DELETED)
                .orElseThrow(() -> new BookNotFoundException(id));
        book.setStatus(Status.DELETED);
        bookRepo.save(book);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public boolean isAvailable(String id) {
        bookRepo.existsByIdAndStatus(id, Status.AVAILABLE);
        return true;
    }

    @Override
    @Transactional
    public boolean markBorrowed(String id) {
        Book book = bookRepo.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
        bookRepo.updateStatus(id, Status.BORROWED);
        publisher.publish(new AvailabilityEvent(id, book.getTitle(), Status.BORROWED));

        return true;
    }

    @Override
    @Transactional
    public boolean markAvailable(String id) {
        Book book = bookRepo.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
        bookRepo.updateStatus(id, Status.AVAILABLE);
        publisher.publish(new AvailabilityEvent(id, book.getTitle(), Status.AVAILABLE));

        return true;
    }
}