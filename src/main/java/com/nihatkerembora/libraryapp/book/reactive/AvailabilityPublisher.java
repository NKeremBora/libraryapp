package com.nihatkerembora.libraryapp.book.reactive;


import com.nihatkerembora.libraryapp.book.model.dto.event.AvailabilityEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Component
public class AvailabilityPublisher {
    private final Sinks.Many<AvailabilityEvent> sink =
            Sinks.many().multicast().onBackpressureBuffer();

    public void publish(AvailabilityEvent event) {
        sink.tryEmitNext(event);
    }

    public Flux<AvailabilityEvent> getFlux() {
        return sink.asFlux();
    }
}
