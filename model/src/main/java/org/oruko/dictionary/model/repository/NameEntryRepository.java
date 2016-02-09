package org.oruko.dictionary.model.repository;

import org.oruko.dictionary.model.NameEntry;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import javax.transaction.Transactional;

/**
 * Created by dadepo on 2/4/15.
 */
@Transactional
public interface NameEntryRepository extends JpaRepository<NameEntry, Long> {

    /**
     * For finding a {@link NameEntry} given the name
     * @param name the name
     * @return {@link NameEntry}
     */
    NameEntry findByName(String name);

    /**
     * for querying from id and limit by count
     * @param id id to start querying from
     * @param pageable the amount of entry to return
     * @return list of {@link NameEntry}
     */
    List<NameEntry> findByIdGreaterThanEqual(Long id, Pageable pageable);
}
