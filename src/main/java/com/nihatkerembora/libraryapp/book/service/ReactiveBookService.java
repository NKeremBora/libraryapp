package com.nihatkerembora.libraryapp.book.service;

import com.nihatkerembora.libraryapp.book.model.dto.event.AvailabilityEvent;
import reactor.core.publisher.Flux;



public interface ReactiveBookService {

    /**
     * Streams availability events for all books.
     * <p>
     * This stream includes:
     * <ul>
     *   <li>An initial snapshot of all existing books and their availability status</li>
     *   <li>Subsequent real-time updates emitted via a reactive publisher</li>
     * </ul>
     * It is intended to be consumed via Server-Sent Events (SSE) or other reactive mechanisms.
     *
     * @return a {@link Flux} emitting {@link AvailabilityEvent} instances
     */
    Flux<AvailabilityEvent> streamAvailability();
}
