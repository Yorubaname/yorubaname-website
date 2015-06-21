package org.oruko.dictionary.events;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Handles {@link org.oruko.dictionary.events.NameSearchedEvent}
 * Created by Dadepo Aderemi.
 */
@Component
public class NameSearchedEventHandler {

    private RecentSearches recentSearches;

    @Autowired
    public NameSearchedEventHandler(RecentSearches recentSearches) {
        this.recentSearches = recentSearches;
    }

    @Subscribe
    @AllowConcurrentEvents
    public void listen(NameSearchedEvent event) {
        // Handle when a name is searched
        try {
            recentSearches.stack(event.getNameSearched());
        } catch (Exception e) {
            //TODO log this
        }
    }
}
