package org.oruko.dictionary.model.repository;

import org.oruko.dictionary.model.NameEntryFeedback;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import javax.transaction.Transactional;

/**
 * Repository for {@link org.oruko.dictionary.model.NameEntryFeedback}
 *
 * Created by Dadepo Aderemi.
 */
@Transactional
public interface NameEntryFeedbackRepository extends CrudRepository<NameEntryFeedback, Long> {
    List<NameEntryFeedback> findByName(String name);
    List<NameEntryFeedback> findAll(Sort sort);
}
