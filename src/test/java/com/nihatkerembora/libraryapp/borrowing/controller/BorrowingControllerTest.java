package com.nihatkerembora.libraryapp.borrowing.controller;


import com.nihatkerembora.libraryapp.base.AbstractRestControllerTest;
import com.nihatkerembora.libraryapp.borrowing.model.dto.request.BorrowRequest;
import com.nihatkerembora.libraryapp.borrowing.model.dto.response.BorrowingDto;
import com.nihatkerembora.libraryapp.borrowing.model.enums.BorrowStatus;
import com.nihatkerembora.libraryapp.borrowing.service.BorrowService;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for {@link BorrowingController}
 */
@ActiveProfiles("test")
class BorrowingControllerTest extends AbstractRestControllerTest {

    @MockitoBean
    BorrowService borrowService;

    /* ---------- BORROW ---------- */

    @Test
    @DisplayName("Borrow book – success (ROLE_USER)")
    void borrowBook_asUser_created() throws Exception {
        // given
        String bookId = UUID.randomUUID().toString();
        BorrowRequest req = new BorrowRequest(bookId);

        BorrowingDto resp = BorrowingDto.builder()
                .id(UUID.randomUUID().toString())
                .bookId(bookId)
                .borrowedAt(LocalDateTime.now())
                .dueAt(LocalDateTime.now().plusDays(14))
                .status(BorrowStatus.BORROWED.toString())
                .build();

        given(borrowService.borrowBook(bookId)).willReturn(resp);

        // when & then
        mockMvc.perform(post("/api/v1/borrowings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockUserToken.getAccessToken()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookId").value(bookId));

        then(borrowService).should(times(1)).borrowBook(bookId);
    }

    @Test
    @DisplayName("Borrow book – unauthorized when no token")
    void borrowBook_noToken_unauthorized() throws Exception {
        mockMvc.perform(post("/api/v1/borrowings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BorrowRequest("dummy"))))
                .andExpect(status().isUnauthorized());

        then(borrowService).shouldHaveNoInteractions();
    }

    /* ---------- RETURN ---------- */

    @Test
    @DisplayName("Return book – success (ROLE_USER)")
    void returnBook_ok() throws Exception {
        // given
        String id = UUID.randomUUID().toString();
        BorrowingDto returned = BorrowingDto.builder()
                .id(id)
                .bookId(UUID.randomUUID().toString())
                .status(BorrowStatus.RETURNED.toString())
                .returnedAt(LocalDateTime.now())
                .build();

        given(borrowService.returnBook(id)).willReturn(returned);

        // when & then
        mockMvc.perform(put("/api/v1/borrowings/{id}/return", id)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockUserToken.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RETURNED"));
    }

    @Test
    @DisplayName("Return book – unauthorized when no token")
    void returnBook_unauthorized_noToken() throws Exception {
        mockMvc.perform(put("/api/v1/borrowings/{id}/return", UUID.randomUUID()))
                .andExpect(status().isUnauthorized());

        then(borrowService).shouldHaveNoInteractions();
    }

    /* ---------- SEARCH ---------- */

    @Test
    @DisplayName("Search borrowings – paged result")
    void getBorrowings_paged() throws Exception {
        // given
        BorrowingDto first = BorrowingDto.builder()
                .id(UUID.randomUUID().toString())
                .bookId(UUID.randomUUID().toString())
                .status(BorrowStatus.BORROWED.toString())
                .borrowedAt(LocalDateTime.now())
                .dueAt(LocalDateTime.now().plusDays(7))
                .build();

        BorrowingDto second = BorrowingDto.builder()
                .id(UUID.randomUUID().toString())
                .bookId(UUID.randomUUID().toString())
                .status(BorrowStatus.RETURNED.toString())
                .borrowedAt(LocalDateTime.now().minusDays(10))
                .dueAt(LocalDateTime.now().minusDays(3))
                .returnedAt(LocalDateTime.now().minusDays(2))
                .build();

        Page<BorrowingDto> page = new PageImpl<>(List.of(first, second));
        given(borrowService.searchBorrowings(any(), any(), any(), any(), any(), any(), any(Pageable.class)))
                .willReturn(page);

        // when & then
        mockMvc.perform(get("/api/v1/borrowings")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockUserToken.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElementCount").value(2));
    }

    @Test
    @DisplayName("Search borrowings – unauthorized when no token")
    void getBorrowings_unauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/borrowings"))
                .andExpect(status().isUnauthorized());

        then(borrowService).shouldHaveNoInteractions();
    }

    /* ---------- OVERDUE PDF ---------- */

    @Nested
    class OverduePdf {

        @Test
        @DisplayName("Download overdue PDF – success (ROLE_ADMIN)")
        void overduePdf_asAdmin_ok() throws Exception {
            byte[] pdfBytes = "dummy-pdf".getBytes();
            given(borrowService.getOverdueBorrowingsPdf(any(Pageable.class))).willReturn(pdfBytes);

            mockMvc.perform(get("/api/v1/borrowings/overdue")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockAdminToken.getAccessToken()))
                    .andExpect(status().isOk())
                    .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=overdue_borrowings.pdf"))
                    .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                    .andExpect(content().bytes(pdfBytes));
        }

        @Test
        @DisplayName("Download overdue PDF – forbidden for ROLE_USER")
        void overduePdf_asUser_forbidden() throws Exception {
            mockMvc.perform(get("/api/v1/borrowings/overdue")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockUserToken.getAccessToken()))
                    .andExpect(status().isForbidden());

            then(borrowService).shouldHaveNoInteractions();
        }
    }
}
