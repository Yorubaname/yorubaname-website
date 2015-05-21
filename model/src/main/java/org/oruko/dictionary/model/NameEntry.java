package org.oruko.dictionary.model;


import org.hibernate.validator.constraints.NotEmpty;
import org.oruko.dictionary.elasticsearch.IndexedNameEntry;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.lang.reflect.Field;

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
     * Gets the name entry represented as {@link NameDto}
     * @return the {@link NameDto}
     */
    @Transient
    public NameDto toNameDto() {
        NameDto asName = new NameDto(name);
        asName.setEtymology(etymology);
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

    @Transient
    public IndexedNameEntry toIndexEntry() {
        IndexedNameEntry indexedNameEntry = new IndexedNameEntry();
        indexedNameEntry.setName(name);
        indexedNameEntry.setPronunciation(pronunciation);
        indexedNameEntry.setIpaNotation(ipaNotation);
        indexedNameEntry.setMeaning(meaning);
        indexedNameEntry.setExtendedMeaning(extendedMeaning);
        indexedNameEntry.setMorphology(morphology);
        indexedNameEntry.setEtymology(etymology);
        indexedNameEntry.setGeoLocation(geoLocation.toString());
        indexedNameEntry.setVariants(variants);
        indexedNameEntry.setFamousPeople(famousPeople);
        indexedNameEntry.setInOtherLanguages(inOtherLanguages);
        indexedNameEntry.setMedia(media);
        indexedNameEntry.setTags(tags);
        return indexedNameEntry;
    }

    /**
     * Updates properties using another instance of {@link org.oruko.dictionary.model.NameEntry}
     */
    public void update(NameEntry nameEntry) {
        // TODO revisit the reflection API and see if it is possible to prevent the nested for-loop
        Field[] fieldsWithOldValues = this.getClass().getSuperclass().getDeclaredFields();
        Field[] fieldsWithNewValues = nameEntry.getClass().getSuperclass().getDeclaredFields();

        for (Field newField: fieldsWithNewValues) {
            String fieldNameWithNewValue = newField.getName();
            if (fieldNameWithNewValue.equalsIgnoreCase("id")) {
                continue;
            }

            for (Field oldField: fieldsWithOldValues) {
                String fieldNameWithOldValue = oldField.getName();
                if (fieldNameWithOldValue.equalsIgnoreCase(fieldNameWithNewValue)) {
                    try {
                        oldField.set(this, newField.get(nameEntry));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
