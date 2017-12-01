package org.oruko.dictionary.service;

import org.oruko.dictionary.model.NameEntry;
import org.oruko.dictionary.model.State;
import org.oruko.dictionary.model.repository.NameEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class JpaSearchService implements SearchService {
    private NameEntryRepository nameEntryRepository;

    @Autowired
    public JpaSearchService(NameEntryRepository nameEntryRepository) {
        this.nameEntryRepository = nameEntryRepository;
    }

    @Override
    public NameEntry getByName(String nameQuery) {
        return nameEntryRepository.findByNameAndState(nameQuery, State.PUBLISHED);
    }

    @Override
    public Set<Map<String, Object>> search(String searchTerm) {
        /**
         * The following approach should be taken:
         *
         * 1. First do a exact search. If found return result. If not go to 2.
         * 2. Do a search with ascii-folding. If found return result, if not go to 3
         * 3. Do a prefix search. If found, return result, if not go to 4
         * 4. Do a search based on partial match. Irrespective of outcome, proceed to 4
         *    4a - Using nGram?
         *    4b - ?
         * 5. Do a full text search against other variants. Irrespective of outcome, proceed to 6
         * 6. Do a full text search against meaning. Irrespective of outcome, proceed to 7
         * 7. Do a full text search against extendedMeaning
         */
        return null;
    }

    @Override
    public Set<NameEntry> listByAlphabet(String alphabetQuery) {
        return nameEntryRepository.findByNameStartingWithAndState(alphabetQuery, State.PUBLISHED);
    }

    @Override
    public List<String> autocomplete(String query) {
        // TODO implement, disregarding the accents
        return null;
    }

    @Override
    public Integer getSearchableNames() {
        return nameEntryRepository.countByState(State.PUBLISHED);
    }

    @Override
    public IndexOperationStatus bulkIndexName(List<NameEntry> entries) {
        return null;
    }

    public IndexOperationStatus removeFromIndex(String name) {
        NameEntry foundName = nameEntryRepository.findByNameAndState(name, State.PUBLISHED);
        if (foundName == null) {
            return new IndexOperationStatus(false, "Published Name not found");
        }
        foundName.setState(State.UNPUBLISHED);
        nameEntryRepository.save(foundName);
        return new IndexOperationStatus(true, name + " removed from index");
    }

    @Override
    public IndexOperationStatus bulkDeleteFromIndex(List<String> name) {
        return null;
    }
}
