package org.oruko.dictionary.web;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.runners.*;
import org.oruko.dictionary.model.DuplicateNameEntry;
import org.oruko.dictionary.model.NameEntry;
import org.oruko.dictionary.model.exception.RepositoryAccessError;
import org.oruko.dictionary.model.repository.DuplicateNameEntryRepository;
import org.oruko.dictionary.model.repository.NameEntryRepository;

import java.util.Arrays;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
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
    public void testInsertTakingCareOfDuplicates_with_duplicates_and_name_not_in_variant() throws Exception {
        String testName = "Ajani";
        NameEntry nameEntryMock = mock(NameEntry.class);
        when(nameEntryMock.getVariants()).thenReturn(null);
        when(nameEntry.getName()).thenReturn(testName);
        when(nameEntryRepository.findAll()).thenReturn(Arrays.asList(nameEntryMock));
        when(nameEntryRepository.findByName(testName)).thenReturn(nameEntry);
        nameEntryService.insertTakingCareOfDuplicates(nameEntry);

        verify(duplicateEntryRepository).save(any(DuplicateNameEntry.class));
        verify(nameEntryRepository).findAll();
        verify(nameEntryRepository).findByName(testName);
        verifyZeroInteractions(nameEntryRepository);
    }

    @Test(expected = RepositoryAccessError.class)
    public void testInsertTakingCareOfDuplicates_with_duplicates_and_name_already_in_variant() throws Exception {
        String testName = "Ajani";
        NameEntry nameEntryMock = mock(NameEntry.class);
        when(nameEntryMock.getVariants()).thenReturn("Ajani");
        when(nameEntry.getName()).thenReturn(testName);
        when(nameEntryRepository.findAll()).thenReturn(Arrays.asList(nameEntryMock));
        when(nameEntryRepository.findByName(testName)).thenReturn(nameEntry);
        nameEntryService.insertTakingCareOfDuplicates(nameEntry);

        verifyZeroInteractions(nameEntryRepository);
    }


    @Test
    public void testSave() throws Exception {
        nameEntryService.saveName(nameEntry);
        verify(nameEntryRepository).save(nameEntry);
        verifyNoMoreInteractions(nameEntryRepository);
    }

    @Test
    public void testUpdate() throws Exception {
        NameEntry oldEntry = mock(NameEntry.class);
        when(nameEntryRepository.findByName(anyString())).thenReturn(oldEntry);
        nameEntryService.updateName(nameEntry);
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