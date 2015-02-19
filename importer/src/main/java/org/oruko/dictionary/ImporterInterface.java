package org.oruko.dictionary;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.File;
import java.io.IOException;

/**
 * @author Dadepo Aderemi.
 */
public interface ImporterInterface {

    /**
     * Carry out the importation task.
     * @return
     */
    ImportStatus doImport(File source) throws IOException, InvalidFormatException;
}
