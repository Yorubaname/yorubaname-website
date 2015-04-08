package org.oruko.dictionary.model;


import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Entity for persisting duplicated entries
 *
 * Created by dadepo on 2/4/15.
 */
@Entity
@Table(name = "duplicate_entry")
public class DuplicateNameEntry extends AbstractNameEntry {

    @NotEmpty
    private String name;

    public DuplicateNameEntry() {
    }

    public DuplicateNameEntry(NameEntry entry) {
        this.name = entry.getName();
        this.geoLocation= entry.getGeoLocation();
        this.meaning = entry.getMeaning();
        this.tonalMark = entry.getTonalMark();
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
     * Get's the name entry represented as {@link Name}
     * @return the {@link Name}
     */
    @Transient
    public Name toName() {
        Name asName = new Name(name);
        asName.setEtymology(etymology);
        asName.setExtendedMeaning(extendedMeaning);
        asName.setFamousPeople(famousPeople.toString());
        asName.setGeoLocation(geoLocation);
        asName.setInOtherLanguages(inOtherLanguages);
        asName.setIpaNotation(ipaNotation);
        asName.setMeaning(meaning);
        asName.setMedia(media);
        asName.setMorphology(morphology);
        asName.setPronunciation(pronunciation);
        asName.setSubmittedBy(submittedBy);
        asName.setSyllables(syllables);
        asName.setTags(tags);
        asName.setVariants(variants);
        return asName;
    }
}
