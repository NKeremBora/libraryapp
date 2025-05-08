package com.nihatkerembora.libraryapp.book.service;



import com.nihatkerembora.libraryapp.book.model.dto.request.BookCreateRequest;
import com.nihatkerembora.libraryapp.book.model.dto.request.BookUpdateRequest;
import com.nihatkerembora.libraryapp.book.model.dto.response.BookResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for managing books in the library system.
 * Provides operations for creating, retrieving, searching, updating, deleting,
 * and checking/updating availability of books.
 */
public interface BookService {
    /**
     * Adds a new book to the system.
     *
     * @param req the {@link BookCreateRequest} containing details of the book to create.
     * @return the created {@link BookResponse}.
     */
    BookResponse add(BookCreateRequest req);

    /**
     * Retrieves a book by its ID.
     *
     * @param id the ID of the book.
     * @return the {@link BookResponse} of the found book.
     * @throws com.nihatkerembora.libraryservice.book.exception.BookNotFoundException if no book is found.
     */
    BookResponse get(String id);

    /**
     * Searches for books using optional filters such as title, author, ISBN, and genre.
     *
     * @param title the title of the book (optional).
     * @param author the author of the book (optional).
     * @param isbn the ISBN of the book (optional).
     * @param genre the genre name of the book (optional).
     * @param pageable the pagination and sorting information.
     * @return a {@link Page} of {@link BookResponse} matching the search criteria.
     */
    Page<BookResponse> search(String title, String author, String isbn, String genre, Pageable pageable);

    /**
     * Updates an existing book's details.
     *
     * @param id the ID of the book to update.
     * @param req the {@link BookUpdateRequest} containing updated book details.
     * @return the updated {@link BookResponse}.
     * @throws com.nihatkerembora.libraryservice.book.exception.BookNotFoundException if no book is found.
     */
    BookResponse update(String id, BookUpdateRequest req);

    /**
     * Soft deletes a book by marking its status as deleted.
     *
     * @param id the ID of the book to delete.
     */
    void delete(String id);

    /**
     * Checks if the book is currently available.
     *
     * @param id the ID of the book.
     * @return true if the book is available, false otherwise.
     */
    boolean isAvailable(String id);

    /**
     * Marks a book as borrowed, making it unavailable for others.
     *
     * @param id the ID of the book.
     * @return true if the book was successfully marked as borrowed, false otherwise.
     */
    boolean markBorrowed(String id);

    /**
     * Marks a book as available, making it borrowable again.
     *
     * @param id the ID of the book.
     * @return true if the book was successfully marked as available, false otherwise.
     */
    boolean markAvailable(String id);
}
