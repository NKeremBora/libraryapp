package com.nihatkerembora.libraryapp.book.model.mapper;


import com.nihatkerembora.libraryapp.book.model.dto.request.GenreCreateRequest;
import com.nihatkerembora.libraryapp.book.model.dto.request.GenreUpdateRequest;
import com.nihatkerembora.libraryapp.book.model.dto.response.GenreResponse;
import com.nihatkerembora.libraryapp.book.model.entity.Genre;
import org.springframework.stereotype.Component;

@Component
public class GenreMapper {

    /**
     * Create DTO -> Entity
     */
    public Genre toEntity(GenreCreateRequest req) {
        return Genre.builder()
                .name(req.getName())
                .description(req.getDescription())
                .build();
    }

    /**
     * Entity -> Response DTO
     */
    public GenreResponse toDto(Genre genre) {
        return new GenreResponse(
                genre.getId(),
                genre.getName(),
                genre.getDescription()
        );
    }

    /**
     * Update existing Entity with DTO
     */
    public void updateEntity(Genre genre, GenreUpdateRequest req) {
        if (req.getName() != null) {
            genre.setName(req.getName());
        }
        if (req.getDescription() != null) {
            genre.setDescription(req.getDescription());
        }
    }
}