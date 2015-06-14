package org.oruko.dictionary.web.events;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import org.springframework.stereotype.Component;

/**
 * Handles {@link org.oruko.dictionary.web.events.NameSearchedEvent}
 * Created by Dadepo Aderemi.
 */
@Component
public class NameSearchedEventHandler {

    @Subscribe
    @AllowConcurrentEvents
    public void listen(NameSearchedEvent event) {
        // Handle when a name is searched
    }
}
