package org.oruko.dictionary.importer;


import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.hamcrest.text.IsEqualIgnoringCase;
import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.runners.*;
import org.oruko.dictionary.model.NameEntry;
import org.oruko.dictionary.model.repository.DuplicateNameEntryRepository;
import org.oruko.dictionary.model.repository.NameEntryRepository;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ExcelImporterTest {

    @Mock
    NameEntryRepository repository;

    @Mock
    private NameEntryRepository nameEntryRepository;

    @Mock
    private DuplicateNameEntryRepository duplicateEntryRepository;

    @Mock
    private ImporterValidator validator;

    @Mock
    ColumnOrder columnOrder;

    @InjectMocks
    ImporterInterface importer = new ExcelImporter();

    @Before
    public void setUp() throws Exception {

    }

    @Test @Ignore
    public void testDoImport() throws Exception {
        File file = new ClassPathResource("testdata/right_column_order.xlsx").getFile();
        when(validator.isColumnNameInOrder(any(XSSFSheet.class))).thenReturn(true);
        ImportStatus status = importer.importFile(file);

        ArgumentCaptor<NameEntry> messageCaptor = ArgumentCaptor.forClass(NameEntry.class);
        List<NameEntry> allValues = messageCaptor.getAllValues();

        verify(repository, times(3)).save(messageCaptor.capture());

        // Assert first row
        assertThat(allValues.get(0).getName(), IsEqualIgnoringCase.equalToIgnoringCase("kola"));
        assertThat(allValues.get(0).getTonalMark(), is("DD".toCharArray()));
        assertThat(allValues.get(0).getMeaning(), IsEqualIgnoringCase.equalToIgnoringCase("kola means"));

        // Assert second row
        assertThat(allValues.get(1).getName(), IsEqualIgnoringCase.equalToIgnoringCase("koko"));
        assertThat(allValues.get(1).getTonalMark(), is("RR".toCharArray()));
        assertThat(allValues.get(1).getMeaning(), IsEqualIgnoringCase.equalToIgnoringCase("koko means"));

        // Assert second row
        assertThat(allValues.get(2).getName(), IsEqualIgnoringCase.equalToIgnoringCase("dadepo"));
        assertThat(allValues.get(2).getTonalMark(), is("MM".toCharArray()));
        assertThat(allValues.get(2).getMeaning(), IsEqualIgnoringCase.equalToIgnoringCase("dadepo means"));

        assertEquals(status.hasErrors(), false);

    }
}