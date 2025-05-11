package com.nihatkerembora.libraryapp.borrowing.port.out;

/**
 * Port aracılığıyla kullanıcı durumunu kontrol eder.
 */
public interface UserStatusPort {
    /**
     * Kullanıcının aktif olup olmadığını döner.
     * @param userId kullanıcı ID
     * @return true ise ACTIVE
     */
    boolean isActive(String userId);
}
