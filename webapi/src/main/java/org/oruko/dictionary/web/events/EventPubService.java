package org.oruko.dictionary.web.events;

import com.google.common.eventbus.AsyncEventBus;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;

/**
 * For events publishing
 *
 * Created by Dadepo Aderemi.
 */
@Component
public class EventPubService {

    private final AsyncEventBus eventBus;

    /**
     * public constructor, sets the AsyncEvent bus on construction
     */
    public EventPubService() {
        this.eventBus = new AsyncEventBus(Executors.newCachedThreadPool());
    }

    /**
     * Publishes all events
     * @param events the events to publish
     */
    public void publish(Object... events) {
        Stream.of(events).forEach(event -> {
            this.eventBus.post(event);
        });
    }

    @PostConstruct
    protected void registerListeners() {
        this.eventBus.register(new NameSearchedEventHandler());
        this.eventBus.register(new NameIndexedEventHandler());
    }
}
