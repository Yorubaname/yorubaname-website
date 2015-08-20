package org.oruko.dictionary.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Entity representing the feedback given for a name
 *
 * Created by Dadepo Aderemi.
 */
@Entity
public class NameEntryFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column
    private String name;
    @Column(length = 2000)
    private String feedback;

    /** no args constructor for JPA **/
    public NameEntryFeedback() {
    }

    public NameEntryFeedback(@JsonProperty("name") String name,
                             @JsonProperty("feedback") String feedback) {
        this.name = name;
        this.feedback = feedback;
    }


    public String getName() {
        return name;
    }
    public String getFeedback() {
        return feedback;
    }
}
