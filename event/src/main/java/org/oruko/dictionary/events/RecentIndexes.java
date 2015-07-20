package org.oruko.dictionary.events;

import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Holds recent additions to the search index
 *
 * Created by Dadepo Aderemi.
 */
@Component
public class RecentIndexes {

    //TODO extract this to a property file
    private int limit = 5;
    public void setLimit(int limit) {
        this.limit = limit;
    }

    private Deque<String> deque;

    public RecentIndexes() {
        this.deque = new ArrayDeque<>();
    }

    public void stack(String name) {
        if (deque.contains(name)) {
            deque.remove(name);
        }

        deque.addFirst(name);
        if (deque.size() > limit) {
            deque.removeLast();
        }
    }

    public String[] get() {
        return deque.toArray(new String[deque.size()]);
    }
}
