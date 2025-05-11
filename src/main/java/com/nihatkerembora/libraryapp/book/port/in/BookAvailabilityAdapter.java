package com.nihatkerembora.libraryapp.book.port.in;


import com.nihatkerembora.libraryapp.book.exception.BookNotFoundException;
import com.nihatkerembora.libraryapp.book.model.dto.event.AvailabilityEvent;
import com.nihatkerembora.libraryapp.book.model.entity.Book;
import com.nihatkerembora.libraryapp.book.model.enums.Status;
import com.nihatkerembora.libraryapp.book.publisher.AvailabilityPublisher;
import com.nihatkerembora.libraryapp.book.repository.BookRepository;
import com.nihatkerembora.libraryapp.borrowing.port.out.BookAvailabilityPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Component
@RequiredArgsConstructor
class BookAvailabilityAdapter implements BookAvailabilityPort {
    private final BookRepository bookRepo;
    private final AvailabilityPublisher publisher;

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public boolean isAvailable(String id) {
        return bookRepo.existsByIdAndStatus(id, Status.AVAILABLE);
    }

    @Transactional
    public boolean markBorrowed(String id) {
        Book book = bookRepo.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
        bookRepo.updateStatus(id, Status.BORROWED);
        publisher.publish(new AvailabilityEvent(id, book.getTitle(), Status.BORROWED));

        return true;
    }

    @Transactional
    public boolean markAvailable(String id) {
        Book book = bookRepo.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
        bookRepo.updateStatus(id, Status.AVAILABLE);
        publisher.publish(new AvailabilityEvent(id, book.getTitle(), Status.AVAILABLE));

        return true;
    }
}
