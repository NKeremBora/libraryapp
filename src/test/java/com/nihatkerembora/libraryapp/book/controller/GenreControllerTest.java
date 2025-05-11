package com.nihatkerembora.libraryapp.book.controller;


import com.nihatkerembora.libraryapp.base.AbstractRestControllerTest;
import com.nihatkerembora.libraryapp.book.model.dto.request.GenreCreateRequest;
import com.nihatkerembora.libraryapp.book.model.dto.request.GenreUpdateRequest;
import com.nihatkerembora.libraryapp.book.model.dto.response.GenreResponse;
import com.nihatkerembora.libraryapp.book.service.GenreService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for {@link GenreController}
 */
@ActiveProfiles("test")
class GenreControllerTest extends AbstractRestControllerTest {

    @MockitoBean
    GenreService genreService;

    /* ------------------------------------------------------------------ */
    /* -----------------------  CREATE / POST  -------------------------- */
    /* ------------------------------------------------------------------ */

    @Test
    @DisplayName("POST /api/v1/genres — Admin => 201 CREATED")
    void createGenre_asAdmin_created() throws Exception {

        GenreCreateRequest req = GenreCreateRequest.builder()
                .name("Fantasy")
                .description("Imaginative fiction")
                .build();

        GenreResponse created = GenreResponse.builder()
                .id(UUID.randomUUID().toString())
                .name(req.getName())
                .description(req.getDescription())
                .build();

        given(genreService.create(any(GenreCreateRequest.class))).willReturn(created);

        mockMvc.perform(post("/api/v1/genres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockAdminToken.getAccessToken()))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/genres/" + created.getId()))
                .andExpect(jsonPath("$.id").value(created.getId().toString()))
                .andExpect(jsonPath("$.name").value(req.getName()));

        then(genreService).should(times(1)).create(any(GenreCreateRequest.class));
    }

    @Test
    @DisplayName("POST /api/v1/genres — User => 403 FORBIDDEN")
    void createGenre_asUser_forbidden() throws Exception {
        GenreCreateRequest dummy = GenreCreateRequest.builder()
                .name("X")
                .description("Y")
                .build();

        mockMvc.perform(post("/api/v1/genres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dummy))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockUserToken.getAccessToken()))
                .andExpect(status().isForbidden());

        then(genreService).shouldHaveNoInteractions();
    }

    /* ------------------------------------------------------------------ */
    /* ------------------------  GET BY ID  ----------------------------- */
    /* ------------------------------------------------------------------ */

    @Test
    void getGenre_byId_success() throws Exception {
        UUID id = UUID.randomUUID();

        GenreResponse resp = GenreResponse.builder()
                .id(id.toString())
                .name("Mystery")
                .description("Who-dunnit")
                .build();

        given(genreService.get(id.toString())).willReturn(resp);

        mockMvc.perform(get("/api/v1/genres/{id}", id)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockUserToken.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value(resp.getName()))
                .andExpect(jsonPath("$.description").value(resp.getDescription()));
    }

    @Test
    void getGenre_unauthorized_whenNoToken() throws Exception {
        mockMvc.perform(get("/api/v1/genres/{id}", UUID.randomUUID()))
                .andExpect(status().isUnauthorized());

        then(genreService).shouldHaveNoInteractions();
    }

    /* ------------------------------------------------------------------ */
    /* ------------------------  LIST / PAGE  --------------------------- */
    /* ------------------------------------------------------------------ */

    @Test
    void listGenres_returnsPagedContent() throws Exception {

        GenreResponse g1 = GenreResponse.builder()
                .id(UUID.randomUUID().toString())
                .name("Mystery")
                .description("Detective stories")
                .build();

        GenreResponse g2 = GenreResponse.builder()
                .id(UUID.randomUUID().toString())
                .name("Thriller")
                .description("Suspense")
                .build();

        Page<GenreResponse> page = new PageImpl<>(List.of(g1, g2));
        given(genreService.list(any(Pageable.class))).willReturn(page);

        mockMvc.perform(get("/api/v1/genres")
                        .param("page", "0")
                        .param("size", "10")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockAdminToken.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElementCount").value(2));
    }

    /* ------------------------------------------------------------------ */
    /* -------------------------  UPDATE  ------------------------------- */
    /* ------------------------------------------------------------------ */

    @Test
    void updateGenre_asAdmin_success() throws Exception {
        UUID id = UUID.randomUUID();

        GenreUpdateRequest req = GenreUpdateRequest.builder()
                .name("Sci-Fi")
                .description("Science Fiction")
                .build();

        GenreResponse updated = GenreResponse.builder()
                .id(id.toString())
                .name(req.getName())
                .description(req.getDescription())
                .build();

        given(genreService.update(eq(id.toString()), any(GenreUpdateRequest.class))).willReturn(updated);

        mockMvc.perform(put("/api/v1/genres/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockAdminToken.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value(req.getName()));

        then(genreService).should(times(1)).update(eq(id.toString()), any(GenreUpdateRequest.class));
    }

    @Test
    void updateGenre_asUser_forbidden() throws Exception {
        mockMvc.perform(put("/api/v1/genres/{id}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockUserToken.getAccessToken()))
                .andExpect(status().isForbidden());

        then(genreService).shouldHaveNoInteractions();
    }

    /* ------------------------------------------------------------------ */
    /* ------------------------  DELETE  -------------------------------- */
    /* ------------------------------------------------------------------ */

    @Nested
    class DeleteGenre {

        @Test
        void deleteGenre_asAdmin_noContent() throws Exception {
            UUID id = UUID.randomUUID();
            willDoNothing().given(genreService).delete(id.toString());

            mockMvc.perform(delete("/api/v1/genres/{id}", id)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockAdminToken.getAccessToken()))
                    .andExpect(status().isNoContent());

            then(genreService).should(times(1)).delete(id.toString());
        }

        @Test
        void deleteGenre_asUser_forbidden() throws Exception {
            mockMvc.perform(delete("/api/v1/genres/{id}", UUID.randomUUID())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockUserToken.getAccessToken()))
                    .andExpect(status().isForbidden());

            then(genreService).shouldHaveNoInteractions();
        }
    }
}
