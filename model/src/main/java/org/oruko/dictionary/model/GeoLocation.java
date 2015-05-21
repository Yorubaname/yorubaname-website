package org.oruko.dictionary.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.*;

/**
 * Entity representing geo locations
 *
 * Created by Dadepo Aderemi.
 */
@Entity
@Table(name = "geo_location")
public class GeoLocation {


    @Id
    @Column(name = "place", unique = true)
    private String place;

    @Column(name = "region")
    private String region;

    // for JPA
    public GeoLocation() {}

    public GeoLocation(String place, String region) {
        this.place = place;
        this.region = region;
    }


    public String getPlace() {
        return place;
    }

    public void setPlace(String state) {
        this.place = state;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    @Override
    public String toString() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(this.getPlace());
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
