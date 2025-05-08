package com.nihatkerembora.libraryapp.auth.repository;

import com.nihatkerembora.libraryapp.auth.model.entity.InvalidTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvalidTokenRepository extends JpaRepository<InvalidTokenEntity, String> {

    /**
     * Finds an {@link InvalidTokenEntity} by its token ID.
     *
     * @param tokenId the unique identifier of the JWT token (jti claim)
     * @return an {@link Optional} containing the {@link InvalidTokenEntity} if found, or empty otherwise
     */
    Optional<InvalidTokenEntity> findByTokenId(final String tokenId);

}
