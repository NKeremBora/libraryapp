package com.nihatkerembora.libraryapp.book.model.entity;

import com.nihatkerembora.libraryapp.common.model.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "GENRES")
public class Genre extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID")
    private String id;

    @Column(unique = true, nullable = false, length = 50)
    private String name;

    @Column(length = 500)
    private String description;
}
