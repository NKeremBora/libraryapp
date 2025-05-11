package com.nihatkerembora.libraryapp.book.service.impl;

import com.nihatkerembora.libraryapp.book.model.dto.event.AvailabilityEvent;
import com.nihatkerembora.libraryapp.book.publisher.AvailabilityPublisher;
import com.nihatkerembora.libraryapp.book.repository.BookRepository;
import com.nihatkerembora.libraryapp.book.service.ReactiveBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class ReactiveBookServiceImpl implements ReactiveBookService {

    private final BookRepository bookRepo;
    private final AvailabilityPublisher publisher;

    public Flux<AvailabilityEvent> streamAvailability() {
        Flux<AvailabilityEvent> initialBooksStream = Mono
                .fromCallable(bookRepo::findAll)
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable)
                .map(book -> new AvailabilityEvent(
                        book.getId().toString(),
                        book.getTitle(),
                        book.getStatus()
                ));

        Flux<AvailabilityEvent> changeStream = publisher.getFlux();

        return Flux.concat(initialBooksStream, changeStream);
    }
}
