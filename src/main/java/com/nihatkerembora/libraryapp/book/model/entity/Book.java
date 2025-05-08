package com.nihatkerembora.libraryapp.book.model.entity;


import com.nihatkerembora.libraryapp.book.model.enums.Status;
import com.nihatkerembora.libraryapp.common.model.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "BOOKS")
public class Book extends BaseEntity {

    @Id
    @UuidGenerator
    private String id;

    private String title;

    private String author;

    @Column(nullable = false, unique = true, length = 13)
    private String isbn;

    private LocalDate publicationDate;

    @ManyToMany
    @JoinTable(
            name = "book_genres",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private Status status;
}
