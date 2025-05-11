package com.nihatkerembora.libraryapp.book.controller;

import com.nihatkerembora.libraryapp.base.AbstractRestControllerTest;
import com.nihatkerembora.libraryapp.book.model.dto.request.BookCreateRequest;
import com.nihatkerembora.libraryapp.book.model.dto.request.BookUpdateRequest;
import com.nihatkerembora.libraryapp.book.model.dto.response.BookResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookControllerIntegrationTest extends AbstractRestControllerTest {



    @Test
    void addBook_asAdmin_created() throws Exception {
        BookCreateRequest req = BookCreateRequest.builder()
                .title("Domain-Driven Design")
                .author("Eric Evans")
                .isbn("9732125217")
                .publicationDate(LocalDate.of(2003, 8, 30))
                .genreIds(List.of())
                .build();

        String createdJson = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockAdminToken.getAccessToken()))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        BookResponse created = objectMapper.readValue(createdJson, BookResponse.class);
        assertThat(created.getId()).isNotNull();
        assertThat(created.getTitle()).isEqualTo(req.getTitle());
        assertThat(created.getAuthor()).isEqualTo(req.getAuthor());
    }

    @Test
    void addBook_asUser_forbidden() throws Exception {
        BookCreateRequest dummy = BookCreateRequest.builder()
                .title("X")
                .author("Y")
                .isbn("1234567890")
                .publicationDate(LocalDate.now())
                .genreIds(List.of())
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dummy))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockUserToken.getAccessToken()))
                .andExpect(status().isForbidden());
    }

    @Test
    void getBook_byId_success() throws Exception {
        BookCreateRequest req = BookCreateRequest.builder()
                .title("Refactoring")
                .author("Martin Fowler")
                .isbn("9780201485")
                .publicationDate(LocalDate.of(1999, 7, 8))
                .genreIds(List.of())
                .build();

        String json = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockAdminToken.getAccessToken()))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        BookResponse created = objectMapper.readValue(json, BookResponse.class);


        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/books/{id}", created.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockAdminToken.getAccessToken()))
                .andExpect(status().isOk());
    }

    @Test
    void searchBooks_returnsPagedContent() throws Exception {
        for (String t : List.of("Book A", "Book B")) {
            String uniqueIsbn = String.format("%010d", (long)(Math.random() * 1_000_000_0000L));
            BookCreateRequest r = BookCreateRequest.builder()
                    .title(t)
                    .author("Author")
                    .isbn(uniqueIsbn)
                    .publicationDate(LocalDate.now())
                    .genreIds(List.of())
                    .build();
            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/books")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(r))
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockAdminToken.getAccessToken()))
                    .andExpect(status().isCreated());
        }

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/books")
                        .param("page", "0")
                        .param("size", "10")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockAdminToken.getAccessToken()))
                .andExpect(status().isOk());
    }

    @Test
    void updateBook_asAdmin_success() throws Exception {

        BookCreateRequest create = BookCreateRequest.builder()
                .title("Old")
                .author("X")
                .isbn("1234543780")
                .publicationDate(LocalDate.now())
                .genreIds(List.of())
                .build();

        String body = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(create))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockAdminToken.getAccessToken()))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        BookResponse created = objectMapper.readValue(body, BookResponse.class);

        // update
        BookUpdateRequest upd = BookUpdateRequest.builder()
                .title("New Title")
                .author("X")
                .isbn("1236576789")
                .genreIds(List.of())
                .build();

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/books/{id}", created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(upd))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockAdminToken.getAccessToken()))
                .andExpect(status().isOk());
    }

    @Test
    void deleteBook_asAdmin_noContent() throws Exception {

        BookCreateRequest create = BookCreateRequest.builder()
                .title("ToDelete")
                .author("Z")
                .isbn("0423754321")
                .publicationDate(LocalDate.now())
                .genreIds(List.of())
                .build();

        String body = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(create))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockAdminToken.getAccessToken()))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        BookResponse created = objectMapper.readValue(body, BookResponse.class);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/books/{id}/soft-delete", created.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockAdminToken.getAccessToken()))
                .andExpect(status().isNoContent());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/books/{id}", created.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockAdminToken.getAccessToken()))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateBook_asUser_forbidden() throws Exception {

        BookUpdateRequest request = BookUpdateRequest.builder()
                .title("New Title")
                .author("New Author")
                .isbn("1234567890")
                .publicationDate(LocalDate.now())
                .genreIds(List.of())
                .build();

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/books/{id}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockUserToken.getAccessToken()))
                .andExpect(status().isForbidden());
    }
}
