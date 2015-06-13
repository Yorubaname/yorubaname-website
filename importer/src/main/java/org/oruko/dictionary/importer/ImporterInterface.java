package org.oruko.dictionary.importer;

import java.io.File;

/**
 * @author Dadepo Aderemi.
 */
public interface ImporterInterface {

    /**
     * Carry out the importation task.
     * @return {@link org.oruko.dictionary.importer.ImportStatus}
     */
    ImportStatus doImport(File source);

    //TODO Add method to ImportInterface that returns the format the importer expects the columns to be
}
