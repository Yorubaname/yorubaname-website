package org.oruko.dictionary.importer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dadepo Aderemi.
 */
public class ImportStatus {

    private List<String> errorMessages = new ArrayList<>();
    private int numberOfNamesUpload = 0;

    public Boolean hasErrors() {
        return errorMessages.size() > 0;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public void setErrorMessages(String errorMessages) {
        this.errorMessages.add(errorMessages);
    }

    public void incrementNumberOfNames () {
        ++numberOfNamesUpload;
    }

    public int getNumberOfNamesUpload() {
        return numberOfNamesUpload;
    }
}
