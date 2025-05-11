package com.nihatkerembora.libraryapp.logging.service;

import com.nihatkerembora.libraryapp.logging.entity.LogEntity;

/**
 * Service interface for handling log-related operations.
 */
public interface LogService {

    /**
     * Saves the provided {@link LogEntity} to the database.
     *
     * @param logEntity the log entity to persist
     */
    void saveLogToDatabase(final LogEntity logEntity);

}
