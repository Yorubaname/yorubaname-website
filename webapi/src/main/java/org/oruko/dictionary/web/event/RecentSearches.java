package org.oruko.dictionary.web.event;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Holds the recent searches
 * Created by Dadepo Aderemi.
 */
@Component
public class RecentSearches {

    @Value("${app.search.recencyLimit:5}")
    private int recencyLimit;
    @Value("${app.search.popularListLimit:5}")
    private int popularListLimit;

    public void setPopularListLimit(int popularListLimit) {
        this.popularListLimit = popularListLimit;
    }

    public void setRecencyLimit(int recencyLimit) {
        this.recencyLimit = recencyLimit;
    }

    private List<Map<String, Integer>> searchFrequency;

    private Deque<String> deque;

    public RecentSearches() {
        deque = new ArrayDeque<>();
        searchFrequency = new ArrayList<>();
    }

    /**
     * Adds a searched name to the recently searched deque
     * @param name the searched name
     */
    public void stack(String name) {
        insert(name);
        if (deque.size() > recencyLimit) {
            deque.removeLast();
        }
    }

    public boolean remove(String name) {
        if (deque.contains(name)) {
            deque.remove();
            searchFrequency.removeIf(element -> element.get(name) != null);
            searchFrequency.remove(name);
            return true;
        }
        return false;
    }

    /**
     * Get the names recently searched
     * @return the names recently searched
     */
    public String[] get() {
        return deque.toArray(new String[deque.size()]);
    }

    /**
     * Returns the recently searched names ordered by how often the names have been searched
     * @return a list of searched names mapped to how often they have been searched
     */
    public List<Map<String, Integer>> getSearchFrequency() {
        return getNameWithSearchFrequency();
    }

    public String[] getMostPopular() {
        List<Map<String, Integer>> frequency = getNameWithSearchFrequency();
        List<String> mostPopular = new ArrayList<>();

        frequency.stream().forEach(name -> {
            mostPopular.add((String) name.keySet().toArray()[0]);
        });

        if (mostPopular.size() > popularListLimit) {
            return mostPopular.subList(0, popularListLimit).toArray(new String[popularListLimit]);
        }
        return mostPopular.toArray(new String[popularListLimit]);
    }

    private void insert(String name) {
        if (deque.contains(name)) {
            deque.remove(name);
        }
        deque.addFirst(name);
        updateFrequency(name);
    }

    private void updateFrequency(String name) {

        boolean newNameSearch = searchFrequency.stream().noneMatch(element -> element.containsKey(name));

        if (searchFrequency.size() == 0 || newNameSearch) {
            Map<String, Integer> first = new HashMap<>();
            first.put(name, 0);
            searchFrequency.add(first);
        }

        searchFrequency.stream().filter(element -> {
            if (element.containsKey(name)) {
                return true;
            }
            return false;
        }).map(element -> {
            Integer times = element.get(name);
            element.put(name, ++times);
            return element;
        }).collect(Collectors.toList());
    }

    private List<Map<String, Integer>> getNameWithSearchFrequency() {
        return searchFrequency.stream().sorted((left, right) -> {
            String leftKey = (String) left.keySet().toArray()[0];
            String rightKey = (String) right.keySet().toArray()[0];
            return Integer.compare(right.get(rightKey), left.get(leftKey));
        }).collect(Collectors.toList());
    }
}
