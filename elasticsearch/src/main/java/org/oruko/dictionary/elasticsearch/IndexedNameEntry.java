package org.oruko.dictionary.elasticsearch;

import org.springframework.stereotype.Component;

/**
 * @author Dadepo Aderemi.
 */
@Component
public class IndexedNameEntry {

    private String name;

    public void setName(String name) {
        this.name = name;
    }
}
