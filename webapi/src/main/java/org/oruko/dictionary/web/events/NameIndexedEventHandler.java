package org.oruko.dictionary.web.events;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;

/**
 * Handler for {@link org.oruko.dictionary.web.events.NameIndexedEvent}
 * Created by Dadepo Aderemi.
 */
public class NameIndexedEventHandler {

    @Subscribe
    @AllowConcurrentEvents
    public void listen(NameIndexedEvent event) {
        // Handle when a name is indexed
    }


}
