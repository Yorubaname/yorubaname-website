package org.oruko.dictionary.web.event;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import org.oruko.dictionary.elasticsearch.ElasticSearchService;
import org.oruko.dictionary.events.NameDeletedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Handler for {@link org.oruko.dictionary.events.NameDeletedEvent}
 * @author Dadepo Aderemi.
 */
@Component
public class NameDeletedEventHandler {

    @Autowired
    ElasticSearchService elasticSearchService;
    @Autowired
    RecentIndexes recentIndexes;
    @Autowired
    RecentSearches recentSearches;

    @Subscribe
    @AllowConcurrentEvents
    public void listen(NameDeletedEvent event) {
        // Handle when a name is deleted
        try {
            elasticSearchService.deleteFromIndex(event.getName());
            recentIndexes.remove(event.getName());
            recentSearches.remove(event.getName());
        } catch (Exception e) {
            //TODO log this
        }
    }

}
