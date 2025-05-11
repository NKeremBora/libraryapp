package com.nihatkerembora.libraryapp.book.controller;


import com.nihatkerembora.libraryapp.book.model.dto.request.GenreCreateRequest;
import com.nihatkerembora.libraryapp.book.model.dto.request.GenreUpdateRequest;
import com.nihatkerembora.libraryapp.book.model.dto.response.GenreResponse;
import com.nihatkerembora.libraryapp.book.service.GenreService;
import com.nihatkerembora.libraryapp.common.model.dto.response.CustomPagingResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/genres")
@RequiredArgsConstructor
public class GenreController {
    private final GenreService genreService;


    /**
     * Creates a new genre.
     *
     * @param request The {@link GenreCreateRequest} containing genre details.
     * @return The created {@link GenreResponse}.
     */
    @Operation(
            summary = "Create a new genre",
            description = "Creates a new genre with the specified name and description.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Genre successfully created"),
                    @ApiResponse(responseCode = "400", description = "Invalid request body"),
                    @ApiResponse(responseCode = "409", description = "Genre with same name already exists")
            }
    )
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<GenreResponse> createGenre(
            @Valid @RequestBody GenreCreateRequest request) {
        GenreResponse created = genreService.create(request);
        URI location = URI.create("/api/v1/genres/" + created.getId());
        return ResponseEntity.created(location).body(created);
    }

    /**
     * Retrieves a genre by its ID.
     *
     * @param id The UUID of the genre.
     * @return The {@link GenreResponse} of the found genre.
     */
    @Operation(
            summary = "Get genre by ID",
            description = "Fetches a genre's details by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Genre found"),
                    @ApiResponse(responseCode = "404", description = "Genre not found")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<GenreResponse> getGenre(@PathVariable String id) {
        return ResponseEntity.ok(genreService.get(id));
    }


    /**
     * Lists all genres with pagination.
     *
     * @param pageable Pagination details.
     * @return A page of {@link GenreResponse}.
     */
    @Operation(
            summary = "List genres",
            description = "Lists all genres with pagination support.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Genres listed successfully")
            }
    )
    @GetMapping
    public ResponseEntity<CustomPagingResponse<GenreResponse>> listGenres(Pageable pageable) {
        Page<GenreResponse> page = genreService.list(pageable);
        CustomPagingResponse<GenreResponse> response = CustomPagingResponse.from(page);
        return ResponseEntity.ok(response);
    }

    /**
     * Updates an existing genre.
     *
     * @param id The String of the genre to update.
     * @param request The {@link GenreUpdateRequest} with new genre details.
     * @return The updated {@link GenreResponse}.
     */
    @Operation(
            summary = "Update a genre",
            description = "Updates an existing genre's name or description.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Genre updated successfully"),
                    @ApiResponse(responseCode = "404", description = "Genre not found")
            }
    )
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<GenreResponse> updateGenre(
            @PathVariable String id,
            @Valid @RequestBody GenreUpdateRequest request) {
        return ResponseEntity.ok(genreService.update(id, request));
    }

    /**
     * Deletes a genre by its ID.
     *
     * @param id The String of the genre.
     * @return No content response.
     */
    @Operation(
            summary = "Delete a genre",
            description = "Deletes a genre by marking it as deleted if it is not in use.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Genre deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Genre not found"),
                    @ApiResponse(responseCode = "400", description = "Genre is still in use and cannot be deleted")
            }
    )
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGenre(@PathVariable String id) {
        genreService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

