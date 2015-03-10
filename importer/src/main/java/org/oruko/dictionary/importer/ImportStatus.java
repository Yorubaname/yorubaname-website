package org.oruko.dictionary.importer;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that returns the status of importing names provided in a spreadsheet
 *
 * @author Dadepo Aderemi.
 */
public class ImportStatus {

    private List<String> errorMessages = new ArrayList<>();
    private int numberOfNamesUpload = 0;

    /**
     * Use to check if any error was encountered during the upload operation
     * @return true or false
     */
    public Boolean hasErrors() {
        return errorMessages.size() > 0;
    }

    /**
     * Get the text representation of error messages that occurred during the operation procedure
     * @return returns a list of error messages
     */
    public List<String> getErrorMessages() {
        return errorMessages;
    }

    /**
     * Sets an error encountered
     * @param errorMessages the error message
     */
    public void setErrorMessages(String errorMessages) {
        this.errorMessages.add(errorMessages);
    }

    /**
     * Increments the number of names uploaded
     */
    public void incrementNumberOfNames () {
        ++numberOfNamesUpload;
    }

    /**
     * Returns the number of names uploaded
     * @return number of names uploaded
     */
    public int getNumberOfNamesUpload() {
        return numberOfNamesUpload;
    }
}
