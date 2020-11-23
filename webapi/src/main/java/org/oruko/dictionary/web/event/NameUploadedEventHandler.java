package org.oruko.dictionary.web.event;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import org.oruko.dictionary.events.NameUploadedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Handles {@link org.oruko.dictionary.events.NameUploadedEvent}
 * Created by Dadepo Aderemi.
 */
@Component
public class NameUploadedEventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(NameUploadedEventHandler.class);

    private NameUploadStatus uploadStatus;

    @Autowired
    public NameUploadedEventHandler(NameUploadStatus uploadStatus) {
        this.uploadStatus = uploadStatus;
    }

    @Subscribe
    @AllowConcurrentEvents
    public void listen(NameUploadedEvent event) {
        // Handle when a name is searched
        try {
            uploadStatus.setStatus(event);
        } catch (Exception e) {
            LOG.error("Error occurred while setting name upload status.", e);
        }
    }
}
