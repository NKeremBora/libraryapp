package com.nihatkerembora.libraryapp.book.controller;


import com.nihatkerembora.libraryapp.book.model.dto.event.AvailabilityEvent;
import com.nihatkerembora.libraryapp.book.service.ReactiveBookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;


@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookReactiveController {

    private final ReactiveBookService reactiveBookService; //ReactiveBookService


    /**
     * Streams all books' availability status to clients.
     *
     * Initially emits all current books and their status,
     * then continues to emit only availability changes in real-time.
     *
     * @return A Flux stream of {@link AvailabilityEvent} containing book availability info.
     */
    @Operation(
            summary = "Stream book availability status",
            description = "Streams all books' current availability status on connection and continues streaming status changes in real-time.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Streaming availability events as Server-Sent Events"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping(value = "/availability/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<AvailabilityEvent> streamAvailability() {
        return reactiveBookService.streamAvailability();
    }

}
