package org.oruko.dictionary.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.Embeddable;

/**
 * An embedded video which provides some explanation for the owning entity e.g. a YouTube video explaining the usage of a name.
 * Created by Hafiz Adewuyi.
 */
@Embeddable
public class EmbeddedVideo {


    private String url;

    private String caption;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    @Override
    public String toString() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            // TODO
            // log
        }
        return "";
    }
}
