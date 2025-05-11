package com.nihatkerembora.libraryapp.book.controller;

import com.nihatkerembora.libraryapp.base.AbstractRestControllerTest;
import com.nihatkerembora.libraryapp.book.model.dto.request.GenreCreateRequest;
import com.nihatkerembora.libraryapp.book.model.dto.request.GenreUpdateRequest;
import com.nihatkerembora.libraryapp.book.model.dto.response.GenreResponse;
import com.nihatkerembora.libraryapp.book.repository.GenreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for {@link GenreController}
 */
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class GenreControllerIntegrationTest extends AbstractRestControllerTest {

    @Autowired
    private GenreRepository genreRepository;

    @BeforeEach
    public void cleanDatabase() {
        genreRepository.deleteAllInBatch();
    }

    @Test
    void createGenre_asAdmin_created() throws Exception {
        GenreCreateRequest req = GenreCreateRequest.builder()
                .name("Western")
                .description("The Western is a film genre defined by the American Film Institute ")
                .build();

        String json =
                mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/genres")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req))
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockAdminToken.getAccessToken()))
                        .andExpect(status().isCreated())
                        .andExpect(header().exists("Location"))
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        GenreResponse created = objectMapper.readValue(json, GenreResponse.class);
        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo(req.getName());
        assertThat(created.getDescription()).isEqualTo(req.getDescription());
    }

    @Test
    void createGenre_asUser_forbidden() throws Exception {
        GenreCreateRequest req = GenreCreateRequest.builder()
                .name("Sci-Fi")
                .description("Science-fiction genre.")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/genres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockUserToken.getAccessToken()))
                .andExpect(status().isForbidden());
    }

    /* ------------------------------------------------------------------ */
    /* -----------------------------  GET  ------------------------------ */
    /* ------------------------------------------------------------------ */

    @Test
    void getGenre_byId_success() throws Exception {
        // create first
        GenreCreateRequest create = GenreCreateRequest.builder()
                .name("Anime")
                .description("Anime")
                .build();

        String body = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/genres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(create))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockAdminToken.getAccessToken()))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        GenreResponse created = objectMapper.readValue(body, GenreResponse.class);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/genres/{id}", created.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockAdminToken.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Anime"));
    }

    /* ------------------------------------------------------------------ */
    /* ---------------------------  LIST (PAGE)  ------------------------ */
    /* ------------------------------------------------------------------ */

    @Test
    void listGenres_returnsPagedContent() throws Exception {
        // seed a couple of genres
        for (String name : List.of("Mystery", "Thriller")) {
            GenreCreateRequest req = GenreCreateRequest.builder()
                    .name(name + "_" + UUID.randomUUID())  // ensure unique
                    .description(name + " genre")
                    .build();

            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/genres")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req))
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockAdminToken.getAccessToken()))
                    .andExpect(status().isCreated());
        }

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/genres")
                        .param("page", "0")
                        .param("size", "10")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockAdminToken.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElementCount").value(2));
    }

    /* ------------------------------------------------------------------ */
    /* ----------------------------  UPDATE  ---------------------------- */
    /* ------------------------------------------------------------------ */

    @Test
    void updateGenre_asAdmin_success() throws Exception {
        // create
        GenreCreateRequest create = GenreCreateRequest.builder()
                .name("OldName")
                .description("Desc")
                .build();

        String json = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/genres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(create))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockAdminToken.getAccessToken()))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        GenreResponse created = objectMapper.readValue(json, GenreResponse.class);

        // update
        GenreUpdateRequest upd = GenreUpdateRequest.builder()
                .name("NewName")
                .description("NewDesc")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/genres/{id}", created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(upd))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockAdminToken.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("NewName"));
    }

    @Test
    @DisplayName("PUT /genres/{id} — User forbidden")
    void updateGenre_asUser_forbidden() throws Exception {
        GenreUpdateRequest req = GenreUpdateRequest.builder()
                .name("Whatever")
                .description("Irrelevant")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/genres/{id}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockUserToken.getAccessToken()))
                .andExpect(status().isForbidden());
    }

    /* ------------------------------------------------------------------ */
    /* ---------------------------  DELETE  ----------------------------- */
    /* ------------------------------------------------------------------ */

    @Test
    void deleteGenre_asAdmin_noContent() throws Exception {
        GenreCreateRequest req = GenreCreateRequest.builder()
                .name("TempDel")
                .description("delete me")
                .build();

        String body = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/genres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockAdminToken.getAccessToken()))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        GenreResponse created = objectMapper.readValue(body, GenreResponse.class);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/genres/{id}", created.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockAdminToken.getAccessToken()))
                .andExpect(status().isNoContent());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/genres/{id}", created.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockAdminToken.getAccessToken()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteGenre_asUser_forbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/genres/{id}", UUID.randomUUID())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockUserToken.getAccessToken()))
                .andExpect(status().isForbidden());
    }
}
