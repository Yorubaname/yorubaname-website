package org.oruko.dictionary.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface SearchService {
    Map<String, Object> getByName(String nameQuery);

    Set<Map<String, Object>> search(String searchTerm);

    Set<Map<String, Object>> listByAlphabet(String alphabetQuery);

    List<String> autocomplete(String query);

    Integer getSearchableNames();
}
