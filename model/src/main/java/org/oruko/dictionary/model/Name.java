package org.oruko.dictionary.model;

/**
 * Value object Representation of a name Entry in dictionary
 * Created by dadepo on 2/11/15.
 */
public class Name {

    private String name;
    private Tone tone;
    private String meaning;
    private String geoLocation;
    private String morphology;
    private String submittedBy;
    private String pronunciation;
    private String ipaNotation;
    private String syllables;
    private String extendedMeaning;
    private String etymology;
    private String variants;
    private String famousPeople;
    private String inOtherLanguages;
    private String media;
    private String tags;

    /**
     * Public constructor for creating an instance of @{link Name} using only
     * the name value;
     * @param name
     */
    public Name(String name) {
        this.name = name;
    }

    public Name(String name, Tone tone, String meaning, String geoLocation, String morphology, String submittedBy,
                String pronunciation, String ipaNotation, String syllables, String extendedMeaning,
                String etymology, String variants, String famousPeople, String inOtherLanguages, String media,
                String tags) {
        this.name = name;
        this.tone = tone;
        this.meaning = meaning;
        this.geoLocation = geoLocation;
        this.morphology = morphology;
        this.submittedBy = submittedBy;
        this.pronunciation = pronunciation;
        this.ipaNotation = ipaNotation;
        this.syllables = syllables;
        this.extendedMeaning = extendedMeaning;
        this.etymology = etymology;
        this.variants = variants;
        this.famousPeople = famousPeople;
        this.inOtherLanguages = inOtherLanguages;
        this.media = media;
        this.tags = tags;
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

    /**
     * Get the morphology
     * @return the morphology
     */
    public String getMorphology() {
        return morphology;
    }

    /**
     * Sets the morphology
     * @param morphology the morphology
     */
    public void setMorphology(String morphology) {
        this.morphology = morphology;
    }

    public String getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(String submittedBy) {
        this.submittedBy = submittedBy;
    }

    public String getPronunciation() {
        return pronunciation;
    }

    public void setPronunciation(String pronunciation) {
        this.pronunciation = pronunciation;
    }

    public String getIpaNotation() {
        return ipaNotation;
    }

    public void setIpaNotation(String ipaNotation) {
        this.ipaNotation = ipaNotation;
    }

    public String getSyllables() {
        return syllables;
    }

    public void setSyllables(String syllables) {
        this.syllables = syllables;
    }

    public String getExtendedMeaning() {
        return extendedMeaning;
    }

    public void setExtendedMeaning(String extendedMeaning) {
        this.extendedMeaning = extendedMeaning;
    }

    public String getEtymology() {
        return etymology;
    }

    public void setEtymology(String etymology) {
        this.etymology = etymology;
    }

    public String getVariants() {
        return variants;
    }

    public void setVariants(String variants) {
        this.variants = variants;
    }

    public String getFamousPeople() {
        return famousPeople;
    }

    public void setFamousPeople(String famousPeople) {
        this.famousPeople = famousPeople;
    }

    public String getInOtherLanguages() {
        return inOtherLanguages;
    }

    public void setInOtherLanguages(String inOtherLanguages) {
        this.inOtherLanguages = inOtherLanguages;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}