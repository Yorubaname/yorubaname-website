package org.oruko.dictionary.events;

import com.google.common.eventbus.AsyncEventBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;

/**
 * Manages events publishing.
 *
 * Created by Dadepo Aderemi.
 */
@Component
public class EventPubService {

    private final AsyncEventBus eventBus;

    @Autowired
    private NameSearchedEventHandler nameSearchedEventHandler;

    @Autowired
    private NameIndexedEventHandler nameIndexedEventHandler;

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
        // TODO configuring the event bus should be extracted into a config file
        this.eventBus.register(nameSearchedEventHandler);
        this.eventBus.register(nameIndexedEventHandler);
    }
}
