package org.oruko.dictionary.model.repository;

import org.oruko.dictionary.model.DuplicateNameEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by dadepo on 2/4/15.
 */
@Transactional
public interface DuplicateNameEntryRepository extends JpaRepository<DuplicateNameEntry, Long> {

    /**
     * For finding a {@link org.oruko.dictionary.model.DuplicateNameEntry} given the name
     * @param name the duplicated name
     * @return {@link org.oruko.dictionary.model.DuplicateNameEntry}
     */
    List<DuplicateNameEntry> findByName(String name);

}
