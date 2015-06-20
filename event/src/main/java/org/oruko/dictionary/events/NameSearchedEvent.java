package org.oruko.dictionary.events;

import java.time.LocalDateTime;

/**
 * Event object for search activity
 * Created by Dadepo Aderemi.
 */
public class NameSearchedEvent {

    private final String nameSearched;
    private final String ipOfRequest;
    private final LocalDateTime timestamp = LocalDateTime.now();

    public NameSearchedEvent(String nameSearched, String ipOfRequest) {
        this.nameSearched = nameSearched;
        this.ipOfRequest = ipOfRequest;
    }

    public String getNameSearched() {
        return nameSearched;
    }

    public String getIpOfRequest() {
        return ipOfRequest;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
