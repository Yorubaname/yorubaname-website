package org.oruko.dictionary.service;

import org.oruko.dictionary.model.NameEntry;
import org.oruko.dictionary.model.State;
import org.oruko.dictionary.model.repository.NameEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    public Set<NameEntry> search(String searchTerm) {
        Set<NameEntry> possibleFound = new LinkedHashSet<>();
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
        NameEntry exactFound = nameEntryRepository.findByNameAndState(searchTerm, State.PUBLISHED);
        if (exactFound != null) {
            return Collections.singleton(exactFound);
        }
        Set<NameEntry> startingWithSearchTerm = nameEntryRepository.findByNameStartingWithAndState(searchTerm, State.PUBLISHED);
        if (startingWithSearchTerm != null && startingWithSearchTerm.size() > 0) {
            return startingWithSearchTerm;
        }

        possibleFound.addAll(nameEntryRepository.findNameEntryByNameContainingAndState(searchTerm, State.PUBLISHED));
        possibleFound.addAll(nameEntryRepository.findNameEntryByVariantsContainingAndState(searchTerm, State.PUBLISHED));
        possibleFound.addAll(nameEntryRepository.findNameEntryByMeaningContainingAndState(searchTerm, State.PUBLISHED));
        possibleFound.addAll(nameEntryRepository.findNameEntryByExtendedMeaningContainingAndState(searchTerm, State.PUBLISHED));

        return possibleFound;
    }

    @Override
    public Set<NameEntry> listByAlphabet(String alphabetQuery) {
        return nameEntryRepository.findByNameStartingWithAndState(alphabetQuery, State.PUBLISHED);
    }

    @Override
    public List<String> autocomplete(String query) {
        Set<NameEntry> names = nameEntryRepository.findByNameStartingWithAndState(query, State.PUBLISHED);
        return names.stream().map(NameEntry::getName).collect(Collectors.toList());
    }

    @Override
    public Integer getSearchableNames() {
        return nameEntryRepository.countByState(State.PUBLISHED);
    }

    @Override
    public IndexOperationStatus bulkIndexName(List<NameEntry> entries) {
        if (entries.size() == 0) {
            return new IndexOperationStatus(false, "Cannot index an empty list");
        }
        nameEntryRepository.save(entries);
        return new IndexOperationStatus(true, "Bulk indexing successfully. Indexed the following names "
                + String.join(",", entries.stream().map(NameEntry::getName).collect(Collectors.toList())));
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
    public IndexOperationStatus bulkRemoveByNameFromIndex(List<String> names) {
        if (names.size() == 0) {
            return new IndexOperationStatus(false, "Cannot index an empty list");
        }
        List<NameEntry> nameEntries = names.stream().map(name -> nameEntryRepository.findByNameAndState(name, State.PUBLISHED))
                .collect(Collectors.toList());


        List<NameEntry> namesUnpublished = nameEntries.stream().map(name -> {
            name.setState(State.UNPUBLISHED);
            return name;
        }).collect(Collectors.toList());

        nameEntryRepository.save(namesUnpublished);
        return new IndexOperationStatus(true, "Successfully. "
                + "Removed the following names from search index "
                + String.join(",", names));
    }

    @Override
    public IndexOperationStatus bulkRemoveFromIndex(List<NameEntry> nameEntries) {
        List<NameEntry> namesUnpublished = nameEntries.stream()
                .peek(name -> name.setState(State.UNPUBLISHED))
                .collect(Collectors.toList());

        nameEntryRepository.save(namesUnpublished);
        return new IndexOperationStatus(true, "Successfully. "
                + "Removed the following names from search index "
                + String.join(",", nameEntries.stream().map(NameEntry::getName).collect(Collectors.toList())));
    }
}
