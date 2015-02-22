package org.oruko.dictionary.model;

/**
 * Representation of a name Entry in dictionary
 * Created by dadepo on 2/11/15.
 */
public class Name {

    private String name;
    private Tone tone;
    private String meaning;
    private String geoLocation;


    /**
     * Public constructor for creating an instance of @{link Name} using only
     * the name value;
     * @param name
     */
    public Name(String name) {
        this.name = name;
    }

    public Name(String name, String meaning, String geoLocation, Tone tone) {
        this.name = name;
        this.meaning = meaning;
        this.geoLocation = geoLocation;
        this.tone = tone;
    }


    /**
     * Returns the name as a string
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the {@link org.oruko.dictionary.model.Tone} that represents
     * the tone mark
     * @return
     */
    public Tone getTone() {
        return tone;
    }

    /**
     * Set the {@link org.oruko.dictionary.model.Tone}
     * @param tone the tone
     */
    public void setTone(Tone tone) {
        this.tone = tone;
    }

    /**
     * Get the meaning of the name
     * @return the meaning
     */
    public String getMeaning() {
        return meaning;
    }

    /**
     * Sets the meaning of the name
     * @param meaning the meaning
     */
    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    /**
     * Get the geolocation
     * TODO move this to an enum or db entry
     * @return the geolocation
     */
    public String getGeoLocation() {
        return geoLocation;
    }

    /**
     * Set the geo location
     * @param geoLocation the geolocation
     */
    public void setGeoLocation(String geoLocation) {
        this.geoLocation = geoLocation;
    }

}