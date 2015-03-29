package org.oruko.dictionary.model;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.runners.*;
import org.oruko.dictionary.model.repository.DuplicateNameEntryRepository;
import org.oruko.dictionary.model.repository.NameEntryRepository;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class NameEntryServiceTest {

    @Mock
    NameEntryRepository nameEntryRepository;

    @Mock
    DuplicateNameEntryRepository duplicateEntryRepository;

    // System under test
    @InjectMocks
    private NameEntryService nameEntryService = new NameEntryService();

    NameEntry nameEntry;

    @Before
    public void setUp() {
        nameEntry = mock(NameEntry.class);
    }

    @Test
    public void testInsertTakingCareOfDuplicates_no_duplicates() throws Exception {
        String testName = "Ajani";
        when(nameEntry.getName()).thenReturn(testName);
        when(nameEntryRepository.findByName(testName)).thenReturn(null);
        nameEntryService.insertTakingCareOfDuplicates(nameEntry);

        verify(nameEntryRepository).save(nameEntry);
        verify(nameEntryRepository).findByName(testName);
        verifyZeroInteractions(duplicateEntryRepository);
    }

    @Test
    public void testInsertTakingCareOfDuplicates_with_duplicates() throws Exception {
        String testName = "Ajani";
        when(nameEntry.getName()).thenReturn(testName);
        when(nameEntryRepository.findByName(testName)).thenReturn(nameEntry);
        nameEntryService.insertTakingCareOfDuplicates(nameEntry);

        verify(duplicateEntryRepository).save(any(DuplicateNameEntry.class));
        verify(nameEntryRepository).findByName(testName);
        verifyZeroInteractions(nameEntryRepository);
    }


    @Test
    public void testSave() throws Exception {
        nameEntryService.save(nameEntry);
        verify(nameEntryRepository).save(nameEntry);
        verifyNoMoreInteractions(nameEntryRepository);
    }

    @Test
    public void testUpdate() throws Exception {
        //TODO see how the updates can be verified
        NameEntry oldEntry = mock(NameEntry.class);
        when(nameEntryRepository.findByName(anyString())).thenReturn(oldEntry);
        nameEntryService.update(nameEntry);
        verify(oldEntry).update(nameEntry);
    }

    @Test
    public void testFindAll() throws Exception {
        //TODO

    }

    @Test
    public void testDeleteAllAndDuplicates() throws Exception {
        nameEntryService.deleteAllAndDuplicates();
        verify(nameEntryRepository).deleteAll();
        verify(duplicateEntryRepository).deleteAll();
        verifyNoMoreInteractions(nameEntryRepository);
        verifyZeroInteractions(duplicateEntryRepository);
    }

    @Test
    public void testDeleteInEntry() throws Exception {
        nameEntryService.deleteInEntry(nameEntry);
        verify(nameEntryRepository).delete(nameEntry);
        verifyNoMoreInteractions(nameEntryRepository);
    }

    @Test
    public void testDeleteInDuplicateEntry() throws Exception {
        DuplicateNameEntry duplicateNameEntry = mock(DuplicateNameEntry.class);;;;
        nameEntryService.deleteInDuplicateEntry(duplicateNameEntry);
        verify(duplicateEntryRepository).delete(duplicateNameEntry);
    }
}