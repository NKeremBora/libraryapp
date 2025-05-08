package com.nihatkerembora.libraryapp.book.repository;


import com.nihatkerembora.libraryapp.book.model.entity.Book;
import com.nihatkerembora.libraryapp.book.model.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookRepository extends JpaRepository<Book, String>, JpaSpecificationExecutor<Book> {

    /**
     * Checks if a book with the given ISBN exists and is not marked as deleted.
     *
     * @param isbn the ISBN of the book.
     * @param deletedStatus the status representing "deleted".
     * @return true if such a book exists, false otherwise.
     */
    boolean existsByIsbnAndStatusNot(String isbn, Status deletedStatus);

    /**
     * Checks if a book exists by ID and has the specified status.
     *
     * @param id the ID of the book.
     * @param status the status to check.
     * @return true if such a book exists, false otherwise.
     */
    boolean existsByIdAndStatus(String id, Status status);

    /**
     * Finds a book by ID, ensuring it is not marked as deleted.
     *
     * @param id the ID of the book.
     * @param deletedStatus the status representing "deleted".
     * @return an {@link Optional} containing the found book, or empty if not found.
     */
    Optional<Book> findByIdAndStatusNot(String id, Status deletedStatus);

    /**
     * Updates the status of a book by its ID.
     *
     * @param id the ID of the book.
     * @param status the new status to set.
     */
    @Modifying
    @Query("update Book b set b.status = :status where b.id = :id")
    void updateStatus(@Param("id") String id, @Param("status") Status status);

    /**
     * Checks if any book exists that is associated with the given genre ID.
     *
     * @param genreId the ID of the genre.
     * @return true if any book uses this genre, false otherwise.
     */
    boolean existsByGenres_Id(UUID genreId);
}
