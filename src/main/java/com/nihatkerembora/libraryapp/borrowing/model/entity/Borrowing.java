package com.nihatkerembora.libraryapp.borrowing.model.entity;


import com.nihatkerembora.libraryapp.borrowing.model.enums.BorrowStatus;
import com.nihatkerembora.libraryapp.common.model.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "BORROWINGS")
public class Borrowing extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String bookId;

    @Column(nullable = false)
    private String patronId;

    @Column(nullable = false)
    private LocalDateTime borrowedAt;

    @Column(nullable = false)
    private LocalDateTime dueAt;

    private LocalDateTime returnedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BorrowStatus status;

}