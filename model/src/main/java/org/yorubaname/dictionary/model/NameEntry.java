package org.yorubaname.dictionary.model;

import javax.persistence.*;

/**
 * Created by dadepo on 2/4/15.
 */
@Entity
@Table(name = "name_entry")
public class NameEntry {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String name;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
