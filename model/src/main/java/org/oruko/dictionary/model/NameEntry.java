package org.oruko.dictionary.model;


import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Entity for persisting NameDto entries
 * Created by dadepo on 2/4/15.
 */
@Entity
@Table(name = "name_entry")
public class NameEntry extends AbstractNameEntry implements Comparable<NameEntry> {

    @Column(unique=true)
    @NotNull
    @NotEmpty
    private String name;

    public NameEntry() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public NameEntry(String name) {
        this.name = name;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
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
     * Updates properties using another instance of {@link org.oruko.dictionary.model.NameEntry}
     * The state of the update name entry will be changed to modified
     */
    public void update(NameEntry nameEntry) {
        BeanUtils.copyProperties(nameEntry, this);
        // TODO revisit how to get this done on the entity level: how to get @Temporary working with LocalDateTime
        this.setState(State.MODIFIED);
        this.setUpdatedAt(LocalDateTime.now());
    }

    /**
     * Implements the sorting algorithm for {@link NameEntry}
     *
     * @param nameToCompare the instance of {@link NameEntry} to
     *                      compare with.
     * @return -1, 0 or 1.
     */
    @Override
    public int compareTo(NameEntry nameToCompare) {
        return this.name.compareTo(nameToCompare.getName());
    }
}
