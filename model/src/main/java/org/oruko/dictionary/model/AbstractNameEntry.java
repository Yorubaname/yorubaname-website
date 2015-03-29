package org.oruko.dictionary.model;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Parent abstract class shared by {@link org.oruko.dictionary.model.DuplicateNameEntry} and
 * {@link org.oruko.dictionary.model.AbstractNameEntry}
 * @author Dadepo Aderemi.
 */
@MappedSuperclass
public abstract class AbstractNameEntry {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;

    @Column
    protected char[] tonalMark;
    @Column(length = 1000)
    protected String meaning;
    @Column
    protected String geoLocation;
    @Column(length = 1000)
    protected String morphology;
    @Column(length = 1000)
    protected String submittedBy = "Not Available";

    public String getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(String submittedBy) {
        this.submittedBy = submittedBy;
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

    public String getMorphology() {
        return morphology;
    }

    public void setMorphology(String morphology) {
        this.morphology = morphology;
    }

}
