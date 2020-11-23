package org.oruko.dictionary.web.event;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import org.oruko.dictionary.events.NameSearchedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Handles {@link org.oruko.dictionary.events.NameSearchedEvent}
 * Created by Dadepo Aderemi.
 */
@Component
public class NameSearchedEventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(NameSearchedEventHandler.class);

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
            LOG.error("Error occurred while adding a searched name to the recently searched deque.", e);
        }
    }
}
