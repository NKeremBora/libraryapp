package com.nihatkerembora.libraryapp.auth.model.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Represents paging parameters for paginated API requests.
 */
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class CustomPaging {

    @Min(value = 0, message = "Page number must be bigger than 0")
    private Integer pageNumber;

    @Min(value = 1, message = "Page size must be bigger than 0")
    private Integer pageSize;


}
