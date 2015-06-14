package org.oruko.dictionary.web.events;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import javax.annotation.PostConstruct;

/**
 * For instantiating and registering  
 * Created by Dadepo Aderemi.
 */
@Component
public class EventBusFactory {

    private final AsyncEventBus eventBus;

    public EventBusFactory() {
        this.eventBus = new AsyncEventBus(Executors.newCachedThreadPool());
    }

    public EventBus getEventBus() {
        return this.eventBus;
    }


    @PostConstruct
    protected void registerListeners() {
        this.eventBus.register(new NameSearchedEventHandler());
        this.eventBus.register(new NameIndexedEventHandler());
    }
}
