package org.oruko.dictionary.web.event;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import org.oruko.dictionary.events.NameDeletedEvent;
import org.oruko.dictionary.search.api.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Handler for {@link NameDeletedEvent}
 * @author Dadepo Aderemi.
 */
@Component
public class NameDeletedEventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(NameDeletedEventHandler.class);

    // TODO should not be hardwiring a bean here
    @Qualifier("jpaSearchService")
    @Autowired
    SearchService nameSearchService;
    @Autowired
    RecentIndexes recentIndexes;
    @Autowired
    RecentSearches recentSearches;

    @Subscribe
    @AllowConcurrentEvents
    public void listen(NameDeletedEvent event) {
        // Handle when a name is deleted
        try {
            nameSearchService.removeFromIndex(event.getName());
            recentIndexes.remove(event.getName());
            recentSearches.remove(event.getName());
        } catch (Exception e) {
            LOG.error("Error occurred while removing event name.", e);
        }
    }

}
