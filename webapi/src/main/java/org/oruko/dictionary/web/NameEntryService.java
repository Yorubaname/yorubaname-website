package org.oruko.dictionary.web;

import org.oruko.dictionary.model.DuplicateNameEntry;
import org.oruko.dictionary.model.NameEntry;
import org.oruko.dictionary.model.SuggestedName;
import org.oruko.dictionary.model.exception.RepositoryAccessError;
import org.oruko.dictionary.model.repository.DuplicateNameEntryRepository;
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
 * @author Dadepo Aderemi.
 */
@Service
public class NameEntryService {

    private Integer PAGE = 0;
    private Integer COUNT_SIZE = 50;

    private NameEntryRepository nameEntryRepository;
    private DuplicateNameEntryRepository duplicateEntryRepository;
    private SuggestedNameRepository suggestedNameRepository;

    /**
     * Public constructor for {@link NameEntryService} depends on instances of
     * {@link NameEntryRepository} and {@link DuplicateNameEntryRepository}
     * @param nameEntryRepository Repository responsible for persisting {@link NameEntry}
     * @param duplicateEntryRepository Repository responsoble for persisting {@link DuplicateNameEntry}
     */
    @Autowired
    public NameEntryService(NameEntryRepository nameEntryRepository,
                            DuplicateNameEntryRepository duplicateEntryRepository,
                            SuggestedNameRepository suggestedNameRepository) {
        this.nameEntryRepository = nameEntryRepository;
        this.duplicateEntryRepository = duplicateEntryRepository;
        this.suggestedNameRepository = suggestedNameRepository;
    }

    /**
     * Adds a new name if not present. If already present, adds the name to the
     * duplicate table.
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
     * Persist the suggested name
     * @param suggestedName
     */
    public void addSuggestedName(SuggestedName suggestedName) {
        suggestedNameRepository.save(suggestedName);
    }

    /**
     * Returns all the suggested names
     * @return List of {@link SuggestedName}
     */
    public List<SuggestedName> loadAllSuggestedNames() {
        return suggestedNameRepository.findAll();
    }

    /**
     * Saves {@link org.oruko.dictionary.model.NameEntry}
     * @param entry the entry to be saved
     */
    public NameEntry saveName(NameEntry entry) {
        return nameEntryRepository.save(entry);
    }

    /**
     * Updates the properties with values from another {@link org.oruko.dictionary.model.NameEntry}
     * @param newEntry the nameEntry to take values used for updating
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
     * Used to retrieve {@link org.oruko.dictionary.model.NameEntry} from the repository. Supports ability to
     * specify how many to retrieve and pagination.
     * @param pageNumberParam specifies page number
     * @param countParam specifies the count of result
     * @return a list of {@link org.oruko.dictionary.model.NameEntry}
     */
    public List<NameEntry> loadAllNames(Optional<Integer> pageNumberParam, Optional<Integer> countParam) {

        List<NameEntry> nameEntries = new ArrayList<>();
        Integer pageNumber = pageNumberParam.orElse(PAGE);
        Integer count = countParam.orElse(COUNT_SIZE);

        PageRequest request =
                new PageRequest(pageNumber == 0 ? 0 : pageNumber - 1, count, Sort.Direction.ASC, "id");

        Page<NameEntry> pages = nameEntryRepository.findAll(request);
        pages.forEach(page->{
            nameEntries.add(page);
        });

        return nameEntries;
    }

    /**
     * Used to retrieve a {@link org.oruko.dictionary.model.NameEntry} from the repository using its known name
     * @param name the name
     * @return the NameEntry
     */
    public NameEntry loadName(String name) {
        return nameEntryRepository.findByName(name);
    }

    /**
     * Used to retrieve the duplicate entries for the given name string
     * @param name the name
     * @return a list of {@link org.oruko.dictionary.model.DuplicateNameEntry}
     */
    public List<DuplicateNameEntry> loadNameDuplicates(String name) {
        return duplicateEntryRepository.findByName(name);
    }

    /**
     * Duplicates all the name entry plus the duplicates
     */
    // TODO Method level security should be added here
    public void deleteAllAndDuplicates() {
        nameEntryRepository.deleteAll();
        duplicateEntryRepository.deleteAll();
    }

    /**
     * Deletes all {@link org.oruko.dictionary.model.NameEntry}
     */
    // TODO Method level security should be added here
    public void deleteInEntry(NameEntry entry) {
        nameEntryRepository.delete(entry);
    }

    // TODO Method level security should be added here
    public void deleteNameEntryAndDuplicates(String name) {
        NameEntry nameEntry = nameEntryRepository.findByName(name);
        nameEntryRepository.delete(nameEntry);
        duplicateEntryRepository.delete(new DuplicateNameEntry(nameEntry));
    }

    /**
     * Deletes a duplicated entry
     * @param duplicateNameEntry the duplicated entry to delete
     */
    // TODO Method level security should be added here
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
