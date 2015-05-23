package org.oruko.dictionary.model;


import org.hibernate.validator.constraints.NotEmpty;
import org.oruko.dictionary.model.repository.Etymology;
import org.springframework.beans.BeanUtils;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Entity for persisting NameDto entries
 * Created by dadepo on 2/4/15.
 */
@Entity
@Table(name = "name_entry")
public class NameEntry extends AbstractNameEntry {

    @Column(unique=true)
    @NotEmpty
    private String name;

    @ElementCollection
    protected List<Etymology> etymology;
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

    public List<Etymology> getEtymology() {
        return etymology;
    }

    public void setEtymology(List<Etymology> etymology) {
        this.etymology = etymology;
    }


    /**
     * Gets the name entry represented as {@link NameDto}
     * @return the {@link NameDto}
     */
    @Transient
    public NameDto toNameDto() {
        NameDto asName = new NameDto(name);
        asName.setEtymology(etymology.toString());
        asName.setExtendedMeaning(extendedMeaning);
        asName.setFamousPeople(famousPeople);
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
        asName.isIndexed(isIndexed);
        return asName;
    }

    /**
     * Updates properties using another instance of {@link org.oruko.dictionary.model.NameEntry}
     */
    public void update(NameEntry nameEntry) {
        BeanUtils.copyProperties(nameEntry, this);
    }

}
