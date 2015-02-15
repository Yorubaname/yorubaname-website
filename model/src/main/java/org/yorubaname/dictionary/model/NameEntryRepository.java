package org.yorubaname.dictionary.model;

import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;

/**
 * Created by dadepo on 2/4/15.
 */
@Transactional
public interface NameEntryRepository extends CrudRepository<NameEntry, Long> {

    /**
     * For finding a {@link org.yorubaname.dictionary.model.NameEntry} given the name
     * @param name the name
     * @return {@link org.yorubaname.dictionary.model.NameEntry}
     */
    NameEntry findByName(String name);
}
