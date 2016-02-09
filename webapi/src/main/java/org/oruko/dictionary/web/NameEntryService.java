package org.oruko.dictionary.web;

import org.oruko.dictionary.model.DuplicateNameEntry;
import org.oruko.dictionary.model.NameEntry;
import org.oruko.dictionary.model.NameEntryFeedback;
import org.oruko.dictionary.model.SuggestedName;
import org.oruko.dictionary.model.exception.RepositoryAccessError;
import org.oruko.dictionary.model.repository.DuplicateNameEntryRepository;
import org.oruko.dictionary.model.repository.NameEntryFeedbackRepository;
import org.oruko.dictionary.model.repository.NameEntryRepository;
import org.oruko.dictionary.model.repository.SuggestedNameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The service for managing name entries
 *
 * @author Dadepo Aderemi.
 */
@Service
public class NameEntryService {

    private Integer BATCH_SIZE = 50;
    private Integer PAGE = 0;
    private Integer COUNT_SIZE = 50;

    private NameEntryRepository nameEntryRepository;
    private DuplicateNameEntryRepository duplicateEntryRepository;
    private SuggestedNameRepository suggestedNameRepository;
    private NameEntryFeedbackRepository nameEntryFeedbackRepository;

    /**
     *
     * Public constructor for {@link NameEntryService} depends on instances of
     * {@link NameEntryRepository} and {@link DuplicateNameEntryRepository}
     *
     * @param nameEntryRepository      Repository responsible for persisting {@link NameEntry}
     * @param duplicateEntryRepository Repository responsible for persisting {@link DuplicateNameEntry}
     * @param suggestedNameRepository Repository responsible for persisting {@link SuggestedName}
     * @param nameEntryFeedbackRepository  Repository responsible for persisting {@link NameEntryFeedback}
     */
    @Autowired
    public NameEntryService(NameEntryRepository nameEntryRepository,
                            DuplicateNameEntryRepository duplicateEntryRepository,
                            SuggestedNameRepository suggestedNameRepository,
                            NameEntryFeedbackRepository nameEntryFeedbackRepository) {
        this.nameEntryRepository = nameEntryRepository;
        this.duplicateEntryRepository = duplicateEntryRepository;
        this.suggestedNameRepository = suggestedNameRepository;
        this.nameEntryFeedbackRepository = nameEntryFeedbackRepository;
    }

    /**
     * Adds a new name if not present. If already present, adds the name to the
     * duplicate table.
     *
     * @param entry
     */
    public void insertTakingCareOfDuplicates(NameEntry entry) {
        String name = entry.getName();

        if (!namePresentAsVariant(name)) {
            if (alreadyExists(name)) {
                duplicateEntryRepository.save(new DuplicateNameEntry(entry));
            } else {
                nameEntryRepository.save(entry);
            }
        } else {
            throw new RepositoryAccessError("Given name already exists as a variant entry");
        }
    }


    /**
     * Adds a list of names in bulk if not present. If any of the name is already present, adds the name to the
     * duplicate table.
     *
     * @param entries the list of names
     */
    public void bulkInsertTakingCareOfDuplicates(List<NameEntry> entries) {
        int i = 0;
        for (NameEntry entry : entries) {
            this.insertTakingCareOfDuplicates(entry);
            i++;

            if (i == BATCH_SIZE) {
                nameEntryRepository.flush();
                i = 0;
            }
        }
    }


    /**
     * Returns all the feedback for a name, sorted by time submitted
     *
     * @return the feedback as a list of {@link NameEntryFeedback}
     */
    public List<NameEntryFeedback> getFeedback(NameEntry entry) {
        final Sort sort = new Sort(Sort.Direction.DESC, "submittedAt");
        return nameEntryFeedbackRepository.findByName(entry.getName(), sort);
    }

    /**
     * Saves {@link org.oruko.dictionary.model.NameEntry}
     *
     * @param entry the entry to be saved
     */
    public NameEntry saveName(NameEntry entry) {
        return nameEntryRepository.save(entry);
    }

    /**
     * Saves a list {@link org.oruko.dictionary.model.NameEntry}
     *
     * @param entries the list of name entries to be saved
     */
    public List<NameEntry> saveNames(List<NameEntry> entries) {
        int i = 0;
        List<NameEntry> savedNames = new ArrayList<>();
        for (NameEntry entry : entries) {
            savedNames.add(this.saveName(entry));
            i++;
            if (i == BATCH_SIZE) {
                nameEntryRepository.flush();
                i = 0;
            }
        }
        return savedNames;
    }


    /**
     *     /**
     * Updates the properties with values from another {@link org.oruko.dictionary.model.NameEntry}
     *
     * @param oldEntry the entry to be updated
     * @param newEntry the entry with the new value
     * @return the updated entry
     */
    public NameEntry updateName(NameEntry oldEntry, NameEntry newEntry) {
        String oldEntryName = oldEntry.getName();

        // update main entry
        oldEntry.update(newEntry);

        List<DuplicateNameEntry> oldDuplicateNames = duplicateEntryRepository.findByName(oldEntryName);

        // update all duplicate entries
        oldDuplicateNames.forEach(duplicateNameEntry -> {
            duplicateNameEntry.setName(newEntry.getName());
            duplicateEntryRepository.save(duplicateNameEntry);
        });
        return nameEntryRepository.save(oldEntry);
    }


    /**
     * Updates the properties of a list of names with values from another list of name entries
     *
     * @param nameEntries the new entries
     * @return the updated entries
     */
    public List<NameEntry> bulkUpdateNames(List<NameEntry> nameEntries) {
        List<NameEntry> updated = new ArrayList<>();

        int i = 0;
        for (NameEntry nameEntry : nameEntries) {
            NameEntry oldEntry = this.loadName(nameEntry.getName());
            updated.add(this.updateName(oldEntry, nameEntry));
            i++;

            if (i == BATCH_SIZE) {
                nameEntryRepository.flush();
                duplicateEntryRepository.flush();
                i = 0;
            }
        }
        return updated;
    }

    /**
     * Used to retrieve {@link org.oruko.dictionary.model.NameEntry} from the repository. Supports ability to
     * specify how many to retrieve and pagination.
     *
     * @param pageNumberParam specifies page number
     * @param countParam      specifies the count of result
     * @return a list of {@link org.oruko.dictionary.model.NameEntry}
     */
    public List<NameEntry> loadAllNames(Optional<Integer> pageNumberParam, Optional<Integer> countParam) {

        List<NameEntry> nameEntries = new ArrayList<>();
        Integer pageNumber = pageNumberParam.orElse(PAGE);
        Integer count = countParam.orElse(COUNT_SIZE);

        PageRequest request =
                new PageRequest(pageNumber == 0 ? 0 : pageNumber - 1, count, Sort.Direction.ASC, "id");

        Page<NameEntry> pages = nameEntryRepository.findAll(request);
        pages.forEach(page -> {
            nameEntries.add(page);
        });

        return nameEntries;
    }

    /**
     * Used to retrieve a list of {@link NameEntry} from the repository: specifying the id to start the retrieving
     * from and the amount to return.
     *
     * @param fromId the id to start from
     * @param countParam the count of the result
     * @return a list of {@link NameEntry}
     */
    public List<NameEntry> loadNamesGreaterThanId(Optional<Long> fromId, Optional<Integer> countParam) {
        final Long id = fromId.isPresent() ? fromId.get() : 1L;
        final Integer count = countParam.isPresent() ? countParam.get() : COUNT_SIZE;
        return nameEntryRepository.findByIdGreaterThanEqual(id, new PageRequest(0,count));
    }

    /**
     * Used to retrieve all {@link org.oruko.dictionary.model.NameEntry} from the repository.
     *
     * @return a list of all {@link org.oruko.dictionary.model.NameEntry}
     */
    public List<NameEntry> loadAllNames() {
        return nameEntryRepository.findAll();
    }

    /**
     * Returns the number of names in the database
     *
     * @return number of names
     */
    public Long getNameCount() {
        return nameEntryRepository.count();
    }

    /**
     * Used to retrieve a {@link org.oruko.dictionary.model.NameEntry} from the repository using its known name
     *
     * @param name the name
     * @return the NameEntry
     */
    public NameEntry loadName(String name) {
        return nameEntryRepository.findByName(name);
    }

    /**
     * Used to retrieve the duplicate entries for the given name string
     *
     * @param name the name
     * @return a list of {@link org.oruko.dictionary.model.DuplicateNameEntry}
     */
    public List<DuplicateNameEntry> loadNameDuplicates(String name) {
        return duplicateEntryRepository.findByName(name);
    }

    /**
     * Duplicates all the name entry plus the duplicates
     */
    public void deleteAllAndDuplicates() {
        nameEntryRepository.deleteAll();
        duplicateEntryRepository.deleteAll();
        //TODO introduce an event that all names have been deleted
    }


    /**
     * Duplicates a name entry plus its duplicates
     * @param name the name to delete
     */
    public void deleteNameEntryAndDuplicates(String name) {
        NameEntry nameEntry = nameEntryRepository.findByName(name);
        nameEntryRepository.delete(nameEntry);
        duplicateEntryRepository.delete(new DuplicateNameEntry(nameEntry));
    }

    /**
     * Deletes multiple name entries and their duplicates
     * @param names a list of names to delete their entries and their duplicates
     */
    public void batchDeleteNameEntryAndDuplicates(List<String> names) {
        int i = 0;
        for (String name: names ) {
            this.deleteNameEntryAndDuplicates(name);
            i++;

            if (i == BATCH_SIZE) {
                nameEntryRepository.flush();
                duplicateEntryRepository.flush();
                i = 0;
            }
        }
    }

    /**
     * Deletes a duplicated entry
     *
     * @param duplicateNameEntry the duplicated entry to delete
     */
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

    private boolean namePresentAsVariant(String name) {
        // TODO revisit. Might end up being impacting performance
        List<NameEntry> allNames = nameEntryRepository.findAll();
        return allNames.stream().anyMatch((nameEntry) -> {
            if (nameEntry.getVariants() != null) {
                return nameEntry.getVariants().contains(name);
            }
            return false;
        });
    }

}
