package org.oruko.dictionary.model.repository;

import org.oruko.dictionary.model.GeoLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;

/**
 * Repository for interacting with Geolocations
 * Created by Dadepo Aderemi.
 */
@Transactional
public interface GeoLocationRepository extends JpaRepository<GeoLocation, Long> {
    public GeoLocation findByPlace(String place);
}
