package org.oruko.dictionary.auth;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author Dadepo Aderemi.
 */
public interface DictionaryUserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findOneByEmail(String email);
}
