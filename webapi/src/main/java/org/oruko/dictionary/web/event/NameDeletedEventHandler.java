package org.oruko.dictionary.web.event;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import org.oruko.dictionary.events.NameDeletedEvent;
import org.oruko.dictionary.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Handler for {@link NameDeletedEvent}
 * @author Dadepo Aderemi.
 */
@Component
public class NameDeletedEventHandler {

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
            //TODO log this
        }
    }

}
