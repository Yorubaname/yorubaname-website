package org.oruko.dictionary.auth;

import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;

/**
 * Repository for persiting {@link org.oruko.dictionary.auth.ApiUser}
 * Created by Dadepo Aderemi.
 */
@Transactional
public interface ApiUserRepository extends JpaRepository<ApiUser, Long> {
    ApiUser findByEmail(String email);
}
