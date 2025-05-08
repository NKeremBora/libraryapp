package com.nihatkerembora.libraryapp.auth.repository;

import com.nihatkerembora.libraryapp.auth.model.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, String> {

    /**
     * Checks whether a user exists with the given email address.
     *
     * @param email the email address to check
     * @return {@code true} if a user with the given email exists; {@code false} otherwise
     */
    boolean existsByEmailAndIsDeletedFalse(final String email);

    /**
     * Retrieves a user entity by their email address.
     *
     * @param email the email of the user to retrieve
     * @return an {@link Optional} containing the {@link UserEntity} if found, or empty otherwise
     */
    Optional<UserEntity> findByEmail(final String email);


    @Query("""
    SELECT u FROM UserEntity u
    WHERE (:includeDeleted = true OR u.isDeleted = false)
""")
    Page<UserEntity> findAllWithOptionalDeleted(@Param("includeDeleted") boolean includeDeleted, Pageable pageable);

    /**
     * Soft deletes a user by marking them as deleted.
     *
     * @param id the ID of the user to softly delete
     */
    void deleteById(final String id);
}

