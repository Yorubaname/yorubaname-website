package org.oruko.dictionary;


import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.runners.*;
import org.oruko.dictionary.model.NameEntryRepository;
import org.springframework.core.io.ClassPathResource;

import java.io.File;

@RunWith(MockitoJUnitRunner.class)
public class ExcelImporterTest {


    @Mock
    NameEntryRepository repository;

    @InjectMocks
    ImporterInterface importer = new ExcelImporter();

    @Before
    public void setUp() throws Exception {

    }


    @Test
    public void testDoImport() throws Exception {
        File file = new ClassPathResource("testdata/extract_only_names.xlsx").getFile();


        ImportStatus extract_only_names = importer.doImport(file);
    }
}