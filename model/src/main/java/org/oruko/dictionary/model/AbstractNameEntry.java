package org.oruko.dictionary.model;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

/**
 * 1. Name
 * 2. Pronunciation
 * 3. IPA notation
 * 4. Syllabic breakdown:
 * 5. Meaning
 * 6. Extended Meaning
 * 7. Morphology: Omo-wun-mi (How is this different from 4 again?)
 * 8. GLOSS/Etymology
 * 9. Geo-location
 * 10.Variants (aka See Also/Alternative Spelling)
 * 11.Famous people
 * 12.Similar in other languages
 * 13.MEDIA
 * 14.TAGS:
 * Parent abstract class shared by {@link org.oruko.dictionary.model.DuplicateNameEntry} and
 * {@link org.oruko.dictionary.model.AbstractNameEntry}
 *
 * @author Dadepo Aderemi.
 */
@MappedSuperclass
//TODO revisit the entries and use a more appropriate data type in cases this is necessary
public abstract class AbstractNameEntry {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;

    @Column
    protected String pronunciation;

    @Column
    protected String ipaNotation;

    @Column(length = 1000)
    protected String variants;

    @Column
    protected String syllables;

    @Column(length = 5000)
    protected String meaning;

    @Column(length = 50000)
    protected String extendedMeaning;

    @Column(length = 1000)
    protected String morphology;

    @JoinColumn(name = "geo_location_id")
    @ManyToOne
    protected GeoLocation geoLocation;

    @Column(length = 1000)
    protected String famousPeople;

    @Column(length = 1000)
    protected String inOtherLanguages;

    @Column(length = 1000)
    protected String media;

    @Column
    protected char[] tonalMark;

    @Column(length = 1000)
    protected String tags;

    @Column(length = 1000)
    protected String submittedBy = "Not Available";

    @Column
    protected Boolean isIndexed = false;

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
    public GeoLocation getGeoLocation() {
        return geoLocation;
    }

    /**
     * Sets the geo location
     * @param geoLocation the geo location
     */
    public void setGeoLocation(GeoLocation geoLocation) {
        this.geoLocation = geoLocation;
    }

    public String getMorphology() {
        return morphology;
    }

    public void setMorphology(String morphology) {
        this.morphology = morphology;
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

    public Boolean isIndexed() {
        return isIndexed;
    }

    public void isIndexed(Boolean published) {
        this.isIndexed = published;
    }
}
