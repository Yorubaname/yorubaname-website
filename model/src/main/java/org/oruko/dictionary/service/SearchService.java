package org.oruko.dictionary.service;

import org.oruko.dictionary.model.NameEntry;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface SearchService {
    /**
     * For getting an entry from the search index by name
     *
     * @param nameQuery the name
     * @return the nameEntry or null if name not found
     */
    NameEntry getByName(String nameQuery);

    /**
     * For searching the name entries for a name
     *
     *
     *
     * @param searchTerm the search term
     * @return the list of entries found
     */
    Set<Map<String, Object>> search(String searchTerm);

    /**
     * Return all the names which starts with the given alphabet
     *
     * @param alphabetQuery the given alphabet
     *
     * @return the list of names that starts with the given alphabet
     */
    Set<NameEntry> listByAlphabet(String alphabetQuery);

    /**
     * For getting the list of partial matches for autocomplete
     *
     * @param query the query
     * @return the list of partial matches
     */
    List<String> autocomplete(String query);

    Integer getSearchableNames();

    IndexOperationStatus bulkIndexName(List<NameEntry> entries);
    IndexOperationStatus deleteFromIndex(String name);
    IndexOperationStatus bulkDeleteFromIndex(List<String> name);
}