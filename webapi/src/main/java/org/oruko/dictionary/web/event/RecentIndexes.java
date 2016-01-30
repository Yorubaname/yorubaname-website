package org.oruko.dictionary.web.event;

import org.springframework.beans.factory.annotation.Value;
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

    @Value("${app.search.recentIndexLimit:5}")
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

    public boolean remove(String name) {
        if (deque.contains(name)) {
            deque.remove(name);
            return true;
        }
        return false;
    }

    public String[] get() {
        return deque.toArray(new String[deque.size()]);
    }
}
