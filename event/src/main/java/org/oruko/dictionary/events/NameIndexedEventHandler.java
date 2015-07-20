package org.oruko.dictionary.events;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Handler for {@link org.oruko.dictionary.events.NameIndexedEvent}
 * Created by Dadepo Aderemi.
 */
@Component
public class NameIndexedEventHandler {

    private RecentIndexes recentIndexes;

    @Autowired
    public NameIndexedEventHandler(RecentIndexes recentIndexes) {
        this.recentIndexes = recentIndexes;
    }

    @Subscribe
    @AllowConcurrentEvents
    public void listen(NameIndexedEvent event) {
        // Handle when a name is indexed
        recentIndexes.stack(event.getName());
    }


}
