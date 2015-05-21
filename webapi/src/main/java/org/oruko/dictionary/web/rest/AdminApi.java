package org.oruko.dictionary.web.rest;

import org.oruko.dictionary.model.GeoLocation;
import org.oruko.dictionary.model.repository.GeoLocationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Administrative endpoint
 *
 * Created by Dadepo Aderemi.
 */
@RestController
@RequestMapping("/v1/admin")
public class AdminApi {
    private Logger logger = LoggerFactory.getLogger(AdminApi.class);

    @Autowired
    private GeoLocationRepository geoLocationRepository;

    /**
     * End point for returning the locations a name entry could be from
     *
     * @return the geolocations
     */
    @RequestMapping(value = "/geolocations", method = RequestMethod.GET)
    public ResponseEntity<List<GeoLocation>> listGeoLocations() {
        return new ResponseEntity<List<GeoLocation>>(geoLocationRepository.findAll(), HttpStatus.OK);
    }

}
