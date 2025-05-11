package com.nihatkerembora.libraryapp.common.model.dto.response;

import com.nihatkerembora.libraryapp.common.model.CustomPage;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Represents a generic response object for paginated data.
 *
 * @param <T> the type of content contained in the page
 */
@Getter
@Builder
public class CustomPagingResponse<T> {

    private List<T> content;

    private Integer pageNumber;

    private Integer pageSize;

    private Long totalElementCount;

    private Integer totalPageCount;

    /**
     * Builder class for {@link CustomPagingResponse}.
     *
     * @param <T> the type of content in the response
     */
    public static class CustomPagingResponseBuilder<T> {

        /**
         * Populates paging metadata from a {@link CustomPage} object.
         *
         * @param customPage the source page
         * @param <C> the type of content in the source page
         * @return a builder with metadata populated
         */
        public <C> CustomPagingResponseBuilder<T> of(final CustomPage<C> customPage) {
            return CustomPagingResponse.<T>builder()
                    .pageNumber(customPage.getPageNumber())
                    .pageSize(customPage.getPageSize())
                    .totalElementCount(customPage.getTotalElementCount())
                    .totalPageCount(customPage.getTotalPageCount());
        }

    }

    public static <T> CustomPagingResponse<T> from(final Page<T> page) {
        return CustomPagingResponse.<T>builder()
                .content(page.getContent())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElementCount(page.getTotalElements())
                .totalPageCount(page.getTotalPages())
                .build();
    }

    public static <T> CustomPagingResponse<T> from(final CustomPage<T> customPage) {
        return CustomPagingResponse.<T>builder()
                .content(customPage.getContent())
                .pageNumber(customPage.getPageNumber())
                .pageSize(customPage.getPageSize())
                .totalElementCount(customPage.getTotalElementCount())
                .totalPageCount(customPage.getTotalPageCount())
                .build();
    }

}