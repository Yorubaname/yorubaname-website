package org.oruko.dictionary.model.repository;

import org.oruko.dictionary.model.SuggestedName;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for fetching suggested names
 *
 * Created by Dadepo Aderemi.
 */
public interface SuggestedNameRepository extends JpaRepository<SuggestedName, Long> {

    SuggestedName findByName(String name);
}
