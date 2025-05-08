package com.nihatkerembora.libraryapp.book.repository;

import com.nihatkerembora.libraryapp.book.model.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GenreRepository extends JpaRepository<Genre, UUID> {

}
