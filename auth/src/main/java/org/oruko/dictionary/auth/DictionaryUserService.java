package org.oruko.dictionary.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;

/**
 * @author Dadepo Aderemi.
 */
@Component
public class DictionaryUserService {

    @Autowired
    private DictionaryUserRepository userRepository;

    public Optional<UserEntity> getUserById(long id) {
        return Optional.ofNullable(userRepository.findOne(id));
    }

    public Optional<UserEntity> getUserByEmail(String email) {
        return userRepository.findOneByEmail(email);
    }

    public Collection<UserEntity> getAllUsers() {
        return userRepository.findAll(new Sort("email"));
    }

    public UserEntity create(UserEntity user) {
        return userRepository.save(user);
    }

}
