package org.oruko.dictionary.auth;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Dadepo Aderemi.
 */
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    public UserEntity findByEmail(String email);

}
