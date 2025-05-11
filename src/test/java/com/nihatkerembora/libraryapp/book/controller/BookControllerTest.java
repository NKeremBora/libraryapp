package com.nihatkerembora.libraryapp.book.controller;

import com.nihatkerembora.libraryapp.base.AbstractRestControllerTest;
import com.nihatkerembora.libraryapp.book.model.dto.request.BookCreateRequest;
import com.nihatkerembora.libraryapp.book.model.dto.request.BookUpdateRequest;
import com.nihatkerembora.libraryapp.book.model.dto.response.BookResponse;
import com.nihatkerembora.libraryapp.book.service.BookService;
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

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for {@link BookController}
 */
@ActiveProfiles("test")
class BookControllerTest extends AbstractRestControllerTest {

    @MockitoBean
    BookService bookService;


    @Test
    @DisplayName("Should return 403 when requester is USER")
    void addBook_asUser_forbidden() throws Exception {

        BookCreateRequest request = BookCreateRequest.builder()
                .title("Clean Code")
                .author("Robert C. Martin")
                .isbn("9780132350884")
                .publicationDate(LocalDate.of(2008, 8, 1))
                .genreIds(List.of(UUID.randomUUID().toString()))
                .build();

        // when & then
        mockMvc.perform(post("/api/v1/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockUserToken.getAccessToken()))
                .andExpect(status().isForbidden());

        then(bookService).shouldHaveNoInteractions();
    }

    @Test
    void getBook_byId_success() throws Exception {
        // given
        UUID id = UUID.randomUUID();
        BookResponse resp = BookResponse.builder()
                .id(id.toString())
                .title("Domain-Driven Design")
                .author("Eric Evans")
                .isbn("9780321125217")
                .build();
        given(bookService.get(id.toString())).willReturn(resp);

        // when & then
        mockMvc.perform(get("/api/v1/books/{id}", id)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockUserToken.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.title").value(resp.getTitle()))
                .andExpect(jsonPath("$.author").value(resp.getAuthor()))
                .andExpect(jsonPath("$.isbn").value(resp.getIsbn()));
    }

    @Test
    void getBook_unauthorized_whenNoToken() throws Exception {
        mockMvc.perform(get("/api/v1/books/{id}", UUID.randomUUID().toString()))
                .andExpect(status().isUnauthorized());

        then(bookService).shouldHaveNoInteractions();
    }

    @Test
    void searchBooks_returnsPagedContent() throws Exception {
        // given
        BookResponse first = BookResponse.builder()
                .id(UUID.randomUUID().toString())
                .title("Clean Architecture")
                .author("Robert C. Martin")
                .isbn("9780134494166")
                .build();
        BookResponse second = BookResponse.builder()
                .id(UUID.randomUUID().toString())
                .title("Refactoring")
                .author("Martin Fowler")
                .isbn("9780201485677")
                .build();

        Page<BookResponse> page = new PageImpl<>(List.of(first, second));
        given(bookService.search(any(), any(), any(), any(), any(Pageable.class))).willReturn(page);

        // when & then
        mockMvc.perform(get("/api/v1/books")
                        .param("title", "Clean")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockUserToken.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].title").value("Clean Architecture"))
                .andExpect(jsonPath("$.totalElementCount").value(2));
    }

    @Test
    void updateBook_asAdmin_success() throws Exception {
        // given
        UUID id = UUID.randomUUID();
        BookUpdateRequest req = BookUpdateRequest.builder()
                .title("Clean Code – 2nd Edition")
                .author("Robert C. Martin")
                .isbn("9780132350884")
                .build();

        BookResponse updated = BookResponse.builder()
                .id(id.toString())
                .title(req.getTitle())
                .author(req.getAuthor())
                .isbn(req.getIsbn())
                .build();

        given(bookService.update(eq(id.toString()), any(BookUpdateRequest.class))).willReturn(updated);

        // when & then
        mockMvc.perform(put("/api/v1/books/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockAdminToken.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.title").value(req.getTitle()));
    }

    @Test
    void updateBook_asUser_bad_request() throws Exception {
        mockMvc.perform(put("/api/v1/books/{id}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockUserToken.getAccessToken()))
                .andExpect(status().isBadRequest());

        then(bookService).shouldHaveNoInteractions();
    }

    @Nested
    class SoftDelete {

        @Test
        void deleteBook_asAdmin_noContent() throws Exception {
            String id = UUID.randomUUID().toString();

            willDoNothing().given(bookService).delete(id);

            mockMvc.perform(put("/api/v1/books/{id}/soft-delete", id)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockAdminToken.getAccessToken()))
                    .andExpect(status().isNoContent());

            then(bookService).should(times(1)).delete(id);
        }

        @Test
        void deleteBook_asUser_forbidden() throws Exception {
            mockMvc.perform(put("/api/v1/books/{id}/soft-delete", UUID.randomUUID().toString())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockUserToken.getAccessToken()))
                    .andExpect(status().isForbidden());

            then(bookService).shouldHaveNoInteractions();
        }
    }
}
