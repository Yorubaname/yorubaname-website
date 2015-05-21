package org.oruko.dictionary.web;

import org.oruko.dictionary.model.GeoLocation;
import org.oruko.dictionary.model.repository.GeoLocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.beans.PropertyEditorSupport;

/**
 * Used for converting the Place sent in as a string to an instance of {@link org.oruko.dictionary.model.GeoLocation}
 * Created by Dadepo Aderemi.
 */
@Component
public class GeoLocationTypeConverter extends PropertyEditorSupport {

    private GeoLocationRepository locationRepository;

    @Autowired
    public GeoLocationTypeConverter(GeoLocationRepository locationRepository) {
        super();
        this.locationRepository = locationRepository;
    }

    @Override
    public void setAsText(String place) {
        GeoLocation geolocation = locationRepository.findByPlace(place);
        this.setValue(geolocation);
    }

    @Override
    public String getAsText() {
        GeoLocation location = (GeoLocation) this.getValue();
        return location.toString();
    }
}
