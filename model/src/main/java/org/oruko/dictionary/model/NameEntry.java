package org.oruko.dictionary.model;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Created by dadepo on 2/4/15.
 */
@Entity
@Table(name = "name_entry")
public class NameEntry {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique=true)
    @NotEmpty
    private String name;

    @Column
    private char[] tonalMark;
    @Column
    private String meaning;
    @Column
    private String geoLocation;


    public NameEntry() {
    }

    public NameEntry(String name) {
        this.name = name;
    }

    /**
     * Returns the identifier, in this case the database primary key
     * @return the identifier
     */
    public Long getId() {
        return id;
    }

    /**
     * Get the name
     * @return returns the name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * get the tonal mark
     * @return the tonal mark
     */
    public char[] getTonalMark() {
        return tonalMark;
    }

    /**
     * Set the tonal mark
     * @param tonalMark the total mark
     */
    public void setTonalMark(char[] tonalMark) {
        this.tonalMark = tonalMark;
    }

    /**
     * Get the meaning
     * @return the meaning
     */
    public String getMeaning() {
        return meaning;
    }

    /**
     * Sets the meaning
     * @param meaning the meaning
     */
    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    /**
     * Get the geo location
     * @return the geo location
     */
    public String getGeoLocation() {
        return geoLocation;
    }

    /**
     * Sets the geolocation
     * @param geoLocation the geolocation
     */
    public void setGeoLocation(String geoLocation) {
        this.geoLocation = geoLocation;
    }

    /**
     * Get's the name entry represented as {@link org.oruko.dictionary.model.Name}
     * @return the {@link org.oruko.dictionary.model.Name}
     */
    @Transient
    public Name toName() {
        return new Name(name, meaning, geoLocation, new Tone(tonalMark));
    }
}
