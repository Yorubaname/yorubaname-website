package org.oruko.dictionary.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

/**
 * Class that represent the suggested name by user
 *
 * Created by Dadepo Aderemi.
 */
@Entity
public class SuggestedName {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    @NotEmpty
    private String name;

    @Column(length = 2000)
    private String details;
    @ManyToMany
    private List<GeoLocation> geoLocation;

    @Column
    @Email
    @NotEmpty
    private String email;


    public SuggestedName() {
    }

    @JsonCreator
    public SuggestedName(@JsonProperty("name") String name,
                         @JsonProperty("details") String details,
                         @JsonProperty("geoLocation") List<GeoLocation> geoLocation,
                         @JsonProperty("email") String email) {
        this.name = name;
        this.details = details;
        this.geoLocation = geoLocation;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getDetails() {
        return details;
    }

    public List<GeoLocation> getGeoLocation() {
        return geoLocation;
    }

    public String getEmail() {
        return email;
    }

}
