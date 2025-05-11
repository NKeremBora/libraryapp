package com.nihatkerembora.libraryapp.book.service.impl;


import com.nihatkerembora.libraryapp.book.exception.GenreInUseException;
import com.nihatkerembora.libraryapp.book.exception.GenreNotFoundException;
import com.nihatkerembora.libraryapp.book.model.dto.request.GenreCreateRequest;
import com.nihatkerembora.libraryapp.book.model.dto.request.GenreUpdateRequest;
import com.nihatkerembora.libraryapp.book.model.dto.response.GenreResponse;
import com.nihatkerembora.libraryapp.book.model.entity.Genre;
import com.nihatkerembora.libraryapp.book.model.mapper.GenreMapper;
import com.nihatkerembora.libraryapp.book.repository.BookRepository;
import com.nihatkerembora.libraryapp.book.repository.GenreRepository;
import com.nihatkerembora.libraryapp.book.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class GenreServiceImpl implements GenreService {
    private final GenreRepository repository;
    private final GenreMapper mapper;
    private final BookRepository bookRepo;

    @Override
    public GenreResponse create(GenreCreateRequest request) {
        Genre genre = mapper.toEntity(request);
        Genre saved = repository.save(genre);
        return mapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public GenreResponse get(String id) {
        Genre genre = repository.findById(id)
                .orElseThrow(() -> new GenreNotFoundException(id));
        return mapper.toDto(genre);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GenreResponse> list(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toDto);
    }

    @Override
    public GenreResponse update(String id, GenreUpdateRequest request) {
        Genre genre = repository.findById(id)
                .orElseThrow(() -> new GenreNotFoundException(id));
        mapper.updateEntity(genre, request);
        Genre saved = repository.save(genre);
        return mapper.toDto(saved);
    }

    @Override
    public void delete(String id) {
        Genre genre = repository.findById(id)
                .orElseThrow(() -> new GenreNotFoundException(id));
        if (bookRepo.existsByGenres_Id(id)) {
            throw new GenreInUseException(id);
        }
        repository.delete(genre);
    }
}
