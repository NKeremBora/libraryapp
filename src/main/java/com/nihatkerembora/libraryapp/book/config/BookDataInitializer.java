package com.nihatkerembora.libraryapp.book.config;


import com.nihatkerembora.libraryapp.book.model.entity.Book;
import com.nihatkerembora.libraryapp.book.model.entity.Genre;
import com.nihatkerembora.libraryapp.book.model.enums.Status;
import com.nihatkerembora.libraryapp.book.repository.BookRepository;
import com.nihatkerembora.libraryapp.book.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Order(2)
@RequiredArgsConstructor
@Slf4j
public class BookDataInitializer implements CommandLineRunner {

    private final BookRepository bookRepo;
    private final GenreRepository genreRepo;

    @Override
    @Transactional
    public void run(String... args) {
        if (bookRepo.count() == 0) {
            Map<String, Genre> genreMap = genreRepo.findAll().stream()
                    .collect(Collectors.toMap(Genre::getName, Function.identity()));

            List<Book> initialBooks = List.of(
                    Book.builder()
                            .title("1984")
                            .author("George Orwell")
                            .isbn("9780451524935")
                            .publicationDate(LocalDate.of(1949, 6, 8))
                            .genres(Set.of(genreMap.get("Science Fiction"), genreMap.get("Fiction")))
                            .status(Status.AVAILABLE)
                            .build(),

                    Book.builder()
                            .title("To Kill a Mockingbird")
                            .author("Harper Lee")
                            .isbn("9780061120084")
                            .publicationDate(LocalDate.of(1960, 7, 11))
                            .genres(Set.of(genreMap.get("Fiction")))
                            .status(Status.AVAILABLE)
                            .build(),

                    Book.builder()
                            .title("The Hobbit")
                            .author("J.R.R. Tolkien")
                            .isbn("9780547928227")
                            .publicationDate(LocalDate.of(1937, 9, 21))
                            .genres(Set.of(genreMap.get("Fantasy")))
                            .status(Status.AVAILABLE)
                            .build(),

                    Book.builder()
                            .title("A Brief History of Time")
                            .author("Stephen Hawking")
                            .isbn("9780553380163")
                            .publicationDate(LocalDate.of(1988, 4, 1))
                            .genres(Set.of(genreMap.get("Non-Fiction")))
                            .status(Status.AVAILABLE)
                            .build(),

                    Book.builder()
                            .title("Pride and Prejudice")
                            .author("Jane Austen")
                            .isbn("9780141199078")
                            .publicationDate(LocalDate.of(1813, 1, 28))
                            .genres(Set.of(genreMap.get("Romance"), genreMap.get("Fiction")))
                            .status(Status.AVAILABLE)
                            .build()
            );

            bookRepo.saveAll(initialBooks);
            log.info(">>>>> Initial books loaded: {}", initialBooks.size());
        }
    }
}
