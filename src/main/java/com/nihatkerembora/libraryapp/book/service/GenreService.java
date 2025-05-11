package com.nihatkerembora.libraryapp.book.service;

import com.nihatkerembora.libraryapp.book.model.dto.request.GenreCreateRequest;
import com.nihatkerembora.libraryapp.book.model.dto.request.GenreUpdateRequest;
import com.nihatkerembora.libraryapp.book.model.dto.response.GenreResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for managing genres in the library system.
 * Provides operations to create, retrieve, list, update, and delete genres.
 */
public interface GenreService {

    /**
     * Creates a new genre.
     *
     * @param request the {@link GenreCreateRequest} containing the details of the genre to create.
     * @return the created {@link GenreResponse}.
     */
    GenreResponse create(GenreCreateRequest request);

    /**
     * Retrieves a genre by its ID.
     *
     * @param id the UUID of the genre.
     * @return the {@link GenreResponse} of the found genre.
     */
    GenreResponse get(String id);

    /**
     * Lists all genres with pagination support.
     *
     * @param pageable the pagination and sorting information.
     * @return a {@link Page} of {@link GenreResponse} containing the genres.
     */
    Page<GenreResponse> list(Pageable pageable);

    /**
     * Updates an existing genre's details.
     *
     * @param id the UUID of the genre to update.
     * @param request the {@link GenreUpdateRequest} containing updated genre details.
     * @return the updated {@link GenreResponse}.
     */
    GenreResponse update(String id, GenreUpdateRequest request);

    /**
     * Deletes a genre by its ID.
     * <p>
     * If the genre is currently in use by any book, the operation may fail.
     *
     * @param id the UUID of the genre to delete.
     */
    void delete(String id);
}
