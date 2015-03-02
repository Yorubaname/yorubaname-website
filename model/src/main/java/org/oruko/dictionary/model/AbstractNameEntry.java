package org.oruko.dictionary.model;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * @author Dadepo Aderemi.
 */
@MappedSuperclass
public abstract class AbstractNameEntry {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;

    @Column
    protected char[] tonalMark;
    @Column(length = 1000)
    protected String meaning;
    @Column
    protected String geoLocation;
    @Column(length = 1000)
    protected String morphology;

}
