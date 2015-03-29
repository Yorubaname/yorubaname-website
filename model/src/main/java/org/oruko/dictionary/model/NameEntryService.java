package org.oruko.dictionary.model;

import org.oruko.dictionary.model.repository.DuplicateNameEntryRepository;
import org.oruko.dictionary.model.repository.NameEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Dadepo Aderemi.
 */
//TODO refactor so methods give meaningful feedbacks
@Service
public class NameEntryService {

    private Integer PAGE = 0;
    private Integer COUNT_SIZE = 50;

    @Autowired
    NameEntryRepository nameEntryRepository;

    @Autowired
    DuplicateNameEntryRepository duplicateEntryRepository;

    /**
     * Adds a new name if not present. If already present, adds the name to the
     * duplicate table.
     * @param entry
     */
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

    public void update(NameEntry newEntry) {
        NameEntry oldEntry = nameEntryRepository.findByName(newEntry.getName());
        oldEntry.update(newEntry);
        nameEntryRepository.save(oldEntry);
    }

    public void deleteAll() {
        nameEntryRepository.deleteAll();
    }

    public List<NameEntry> findAll(Optional<Integer> pageParam, Optional<Integer> countParam) {

        List<NameEntry> nameEntries = new ArrayList<>();
        Integer pageNumber = pageParam.orElse(PAGE);
        Integer count = countParam.orElse(COUNT_SIZE);

        PageRequest request =
                new PageRequest(pageNumber == 0 ? 0 : pageNumber - 1, count, Sort.Direction.ASC, "id");

        Page<NameEntry> pages = nameEntryRepository.findAll(request);
        pages.forEach(page->{
            nameEntries.add(page);
        });

        return nameEntries;
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
