package org.oruko.dictionary.model.repository;

import org.oruko.dictionary.model.Diction;
import org.springframework.data.repository.CrudRepository;

/**
 * Interface for storing and retrieving Diction contents
 * Created by dadepo on 2/11/15.
 */
public interface DictionRepository extends CrudRepository<Diction, Long> {

    /**
     * Returns the diction for a given name
     * @param name the name
     * @return the {@link Diction}
     */
    Diction findByName(String name);
}
