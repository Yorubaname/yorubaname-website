package org.oruko.dictionary.model;


import org.hibernate.validator.constraints.NotEmpty;

import java.lang.reflect.Field;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Entity for persisting Name entries
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
     * Gets the name entry represented as {@link org.oruko.dictionary.model.Name}
     * @return the {@link org.oruko.dictionary.model.Name}
     */
    @Transient
    public Name toName() {
        return new Name(name, meaning, morphology, geoLocation, new Tone(tonalMark), submittedBy);
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
