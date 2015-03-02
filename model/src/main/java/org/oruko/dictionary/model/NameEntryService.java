package org.oruko.dictionary.model;

import org.oruko.dictionary.model.repository.DuplicateNameEntryRepository;
import org.oruko.dictionary.model.repository.NameEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Dadepo Aderemi.
 */
//TODO refactor so methods give meaningful feedbacks
@Service
public class NameEntryService {

    @Autowired
    NameEntryRepository nameEntryRepository;

    @Autowired
    DuplicateNameEntryRepository duplicateEntryRepository;

    public void insertTakingCareOfDuplicates(NameEntry entry) {
        if (alreadyExists(entry.getName())) {
            duplicateEntryRepository.save(new DuplicateNameEntry(entry));
        } else {
            nameEntryRepository.save(entry);
        }
    }

    public void save(NameEntry entry) {
        nameEntryRepository.save(entry);
    }

    public void deleteAll() {
        nameEntryRepository.deleteAll();
    }

    public void deleteAllAndDuplicates() {
        nameEntryRepository.deleteAll();
        duplicateEntryRepository.deleteAll();
    }

    public void deleteInEntry(NameEntry entry) {
        nameEntryRepository.delete(entry);
    }

    public void deleteInDuplicateEntry(DuplicateNameEntry duplicateNameEntry) {
        duplicateEntryRepository.delete(duplicateNameEntry);
    }

    // ==================================================== Helpers ====================================================
    private boolean alreadyExists(String name) {
        NameEntry entry = nameEntryRepository.findByName(name);
        if (entry == null) {
            return false;
        }
        return true;
    }

}
