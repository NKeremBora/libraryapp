package com.nihatkerembora.libraryapp.book.service;

import com.nihatkerembora.libraryapp.base.AbstractBaseServiceTest;
import com.nihatkerembora.libraryapp.book.exception.GenreInUseException;
import com.nihatkerembora.libraryapp.book.exception.GenreNotFoundException;
import com.nihatkerembora.libraryapp.book.model.dto.request.GenreCreateRequest;
import com.nihatkerembora.libraryapp.book.model.dto.request.GenreUpdateRequest;
import com.nihatkerembora.libraryapp.book.model.dto.response.GenreResponse;
import com.nihatkerembora.libraryapp.book.model.entity.Genre;
import com.nihatkerembora.libraryapp.book.model.mapper.GenreMapper;
import com.nihatkerembora.libraryapp.book.repository.BookRepository;
import com.nihatkerembora.libraryapp.book.repository.GenreRepository;
import com.nihatkerembora.libraryapp.book.service.impl.GenreServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

class GenreServiceImplTest extends AbstractBaseServiceTest {

    @InjectMocks
    private GenreServiceImpl service;

    @Mock
    private GenreRepository repository;

    @Mock
    private GenreMapper mapper;

    @Mock
    private BookRepository bookRepo;

    private String id;
    private Genre entity;
    private GenreResponse dto;
    private GenreCreateRequest createReq;
    private GenreUpdateRequest updateReq;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID().toString();
        entity = new Genre();
        entity.setId(id);

        createReq = GenreCreateRequest.builder()
                .name("Sci-Fi")
                .description("Science Fiction genre")
                .build();

        updateReq = GenreUpdateRequest.builder()
                .name("Sci-Fi Updated")
                .description("Updated description")
                .build();

        dto = GenreResponse.builder()
                .id(id)
                .name(createReq.getName())
                .description(createReq.getDescription())
                .build();
    }

    @Test
    void create_success() {
        given(mapper.toEntity(createReq)).willReturn(entity);
        given(repository.save(entity)).willReturn(entity);
        given(mapper.toDto(entity)).willReturn(dto);

        GenreResponse result = service.create(createReq);

        assertThat(result).isEqualTo(dto);
        then(repository).should(times(1)).save(entity);
    }

    @Nested
    class GetTests {
        @Test
        @DisplayName("get() → başarılı")
        void get_success() {
            given(repository.findById(id)).willReturn(Optional.of(entity));
            given(mapper.toDto(entity)).willReturn(dto);

            GenreResponse result = service.get(id);

            assertThat(result).isEqualTo(dto);
        }

        @Test
        void get_notFound() {
            given(repository.findById(id)).willReturn(Optional.empty());

            assertThatThrownBy(() -> service.get(id))
                    .isInstanceOf(GenreNotFoundException.class)
                    .hasMessageContaining(id.toString());
        }
    }

    @Test
    void list_success() {
        PageRequest pageReq = PageRequest.of(0, 2);
        Page<Genre> page = new PageImpl<>(List.of(entity), pageReq, 1);
        given(repository.findAll(pageReq)).willReturn(page);
        given(mapper.toDto(entity)).willReturn(dto);

        Page<GenreResponse> result = service.list(pageReq);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).containsExactly(dto);
    }

    @Nested
    class UpdateTests {
        @Test
        void update_success() {
            given(repository.findById(id)).willReturn(Optional.of(entity));
            // mapper.updateEntity sadece entity üzerinde değişiklik yapar
            given(repository.save(entity)).willReturn(entity);
            given(mapper.toDto(entity)).willReturn(dto);

            GenreResponse result = service.update(id, updateReq);

            assertThat(result).isEqualTo(dto);
            then(mapper).should().updateEntity(entity, updateReq);
            then(repository).should().save(entity);
        }

        @Test
        void update_notFound() {
            given(repository.findById(id)).willReturn(Optional.empty());

            assertThatThrownBy(() -> service.update(id, updateReq))
                    .isInstanceOf(GenreNotFoundException.class)
                    .hasMessageContaining(id.toString());

            then(repository).should(never()).save(any());
        }
    }

    @Nested
    class DeleteTests {
        @Test
        @DisplayName("delete() → başarılı")
        void delete_success() {
            given(repository.findById(id)).willReturn(Optional.of(entity));
            given(bookRepo.existsByGenres_Id(id)).willReturn(false);

            service.delete(id);

            then(repository).should().delete(entity);
        }

        @Test
        void delete_notFound() {
            given(repository.findById(id)).willReturn(Optional.empty());

            assertThatThrownBy(() -> service.delete(id))
                    .isInstanceOf(GenreNotFoundException.class)
                    .hasMessageContaining(id.toString());

            then(repository).should(never()).delete(any());
        }

        @Test
        void delete_inUse() {
            given(repository.findById(id)).willReturn(Optional.of(entity));
            given(bookRepo.existsByGenres_Id(id)).willReturn(true);

            assertThatThrownBy(() -> service.delete(id))
                    .isInstanceOf(GenreInUseException.class)
                    .hasMessageContaining(id.toString());

            then(repository).should(never()).delete(any());
        }
    }
}
