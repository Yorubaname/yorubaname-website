package org.oruko.dictionary.importer;

import java.io.File;

/**
 * @author Dadepo Aderemi.
 */
public interface ImporterInterface {

    /**
     * Carry out the importation task.
     * @return
     */
    ImportStatus doImport(File source);
}
