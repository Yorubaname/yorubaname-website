package org.oruko.dictionary.model.repository;

import org.oruko.dictionary.model.DuplicateNameEntry;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import javax.transaction.Transactional;

/**
 * Created by dadepo on 2/4/15.
 */
@Transactional
public interface DuplicateNameEntryRepository extends CrudRepository<DuplicateNameEntry, Long> {

    /**
     * For finding a {@link org.oruko.dictionary.model.DuplicateNameEntry} given the name
     * @param name the duplicated name
     * @return {@link org.oruko.dictionary.model.DuplicateNameEntry}
     */
    List<DuplicateNameEntry> findByName(String name);

}
