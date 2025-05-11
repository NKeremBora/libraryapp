package com.nihatkerembora.libraryapp.borrowing.port.out;

public interface BookAvailabilityPort {

    /**
     * Checks if the book is currently available.
     *
     * @param bookId the ID of the book.
     * @return true if the book is available, false otherwise.
     */
    boolean isAvailable(String bookId);

    /**
     * Marks a book as borrowed, making it unavailable for others.
     *
     * @param bookId the ID of the book.
     * @return true if the book was successfully marked as borrowed, false otherwise.
     */
    boolean markBorrowed(String bookId);

    /**
     * Marks a book as available, making it borrowable again.
     *
     * @param bookId the ID of the book.
     * @return true if the book was successfully marked as available, false otherwise.
     */
    boolean markAvailable(String bookId);
}
