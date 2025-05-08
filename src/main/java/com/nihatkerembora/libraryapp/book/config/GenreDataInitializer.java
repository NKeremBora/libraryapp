package com.nihatkerembora.libraryapp.book.config;


import com.nihatkerembora.libraryapp.book.model.entity.Genre;
import com.nihatkerembora.libraryapp.book.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class GenreDataInitializer implements CommandLineRunner {

    private final GenreRepository genreRepo;

    @Override
    @Transactional
    public void run(String... args) {
        if (genreRepo.count() == 0) {
            List<Genre> initialGenres = List.of(
                    Genre.builder().name("Fiction").description("Fictional works").build(),
                    Genre.builder().name("Non-Fiction").description("Non-fictional works").build(),
                    Genre.builder().name("Mystery").description("Mystery and detective stories").build(),
                    Genre.builder().name("Science Fiction").description("Science fiction works").build(),
                    Genre.builder().name("Fantasy").description("Fantasy worlds and adventures").build(),
                    Genre.builder().name("Biography").description("Biographical works").build(),
                    Genre.builder().name("History").description("Historical books and records").build(),
                    Genre.builder().name("Romance").description("Romantic novels and stories").build(),
                    Genre.builder().name("Thriller").description("Thrilling and suspenseful stories").build(),
                    Genre.builder().name("Horror").description("Horror and scary tales").build(),
                    Genre.builder().name("Children").description("Books for children").build(),
                    Genre.builder().name("Young Adult").description("Young adult fiction").build()
            );
            genreRepo.saveAll(initialGenres);
            log.info(">>>>> Initial book genres loaded: {}", initialGenres.size());
        }
    }
}
