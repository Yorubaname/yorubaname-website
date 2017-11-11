package org.oruko.dictionary.model.repository;

import org.oruko.dictionary.model.NameEntry;
import org.oruko.dictionary.model.State;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

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
     * For retrieving name entries by state. Supports pagenation
     *
     * @param state the state of {@link NameEntry} to load
     * @param pageable the {@link Pageable} to represent pagination intent
     * @return list of {@link NameEntry}
     */
    List<NameEntry> findByState(State state, Pageable pageable);

    /**
     * For retrieving all name entries by state
     * @param state the state of {@link NameEntry} to load
     * @return list of {@link NameEntry}
     */
    List<NameEntry> findByState(State state);

    // Search related repository access
    Set<NameEntry> findByNameStartingWithAndState(String alphabet, State state);
    NameEntry findByNameAndState(String alphabet, State state);
    Integer countByState(State state);
}
