package org.oruko.dictionary.model.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.Embeddable;

/**
 * Entity representing the Etymology part of a name entry.
 * Created by Dadepo Aderemi.
 */
@Embeddable
public class Etymology {


    private String part;
    private String meaning;


    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
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
