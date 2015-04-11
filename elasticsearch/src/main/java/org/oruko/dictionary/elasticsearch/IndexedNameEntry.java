package org.oruko.dictionary.elasticsearch;

import org.springframework.stereotype.Component;
/**
 * @author Dadepo Aderemi.
 */
@Component
public class IndexedNameEntry {

    private String name;
    private String pronunciation;
    private String ipaNotation;
    private String meaning;
    private String extendedMeaning;
    private String morphology;
    private String etymology;
    private String geoLocation;
    private String variants;
    private String famousPeople;
    private String inOtherLanguages;
    private String media;
    private String tags;

    public String getName() {
        return name;
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

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public String getExtendedMeaning() {
        return extendedMeaning;
    }

    public void setExtendedMeaning(String extendedMeaning) {
        this.extendedMeaning = extendedMeaning;
    }

    public String getMorphology() {
        return morphology;
    }

    public void setMorphology(String morphology) {
        this.morphology = morphology;
    }

    public String getEtymology() {
        return etymology;
    }

    public void setEtymology(String etymology) {
        this.etymology = etymology;
    }

    public String getGeoLocation() {
        return geoLocation;
    }

    public void setGeoLocation(String geoLocation) {
        this.geoLocation = geoLocation;
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

    public void setName(String name) {
        this.name = name;
    }
}
