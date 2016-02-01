package org.oruko.dictionary.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.hamcrest.core.IsNot;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.oruko.dictionary.events.EventPubService;
import org.oruko.dictionary.events.NameDeletedEvent;
import org.oruko.dictionary.importer.ImportStatus;
import org.oruko.dictionary.importer.ImporterInterface;
import org.oruko.dictionary.model.DuplicateNameEntry;
import org.oruko.dictionary.model.NameEntry;
import org.oruko.dictionary.model.State;
import org.oruko.dictionary.web.NameEntryService;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.isA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test for {@link org.oruko.dictionary.web.rest.NameApi}
 */
@RunWith(MockitoJUnitRunner.class)
public class NameApiTest extends AbstractApiTest {

    @InjectMocks
    NameApi nameApi;

    @Mock
    private NameEntryService entryService;

    @Mock
    private ImporterInterface importerInterface;

    @Mock
    private EventPubService eventPubService;

    MockMvc mockMvc;
    NameEntry testNameEntry;
    NameEntry anotherTestNameEntry;

    @Before
    public void setUp() {

        LocalDateTimeDeserializer deserializer = LocalDateTimeDeserializer.INSTANCE;
        LocalDateTimeSerializer serializer = LocalDateTimeSerializer.INSTANCE;
        SimpleModule dateTimeSerializer = new SimpleModule("MyModule");
        dateTimeSerializer.addSerializer(serializer);
        dateTimeSerializer.addDeserializer(LocalDateTime.class, deserializer);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(dateTimeSerializer);
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(objectMapper);

        mockMvc = MockMvcBuilders.standaloneSetup(nameApi).setMessageConverters(converter).setHandlerExceptionResolvers(
                createExceptionResolver()).build();
        testNameEntry = new NameEntry("test-entry");
        testNameEntry.setMeaning("test_meaninyig");
        testNameEntry.setExtendedMeaning("test_extended_meaning");

        anotherTestNameEntry = new NameEntry();
    }

    @Test
    public void test_get_all_names_via_get() throws Exception {
        testNameEntry.setName("firstname");
        anotherTestNameEntry.setName("secondname");
        when(entryService.loadAllNames(any(), any())).thenReturn(Arrays.asList(testNameEntry, anotherTestNameEntry));
             mockMvc.perform(get("/v1/names"))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].name", is("firstname")))
                    .andExpect(jsonPath("$[1].name", is("secondname")))
                    .andExpect(status().isOk());
    }

    @Test
    public void test_get_all_names_filtered_by_is_indexed() throws Exception {
        testNameEntry.setIndexed(true);
        anotherTestNameEntry.setIndexed(false);
        when(entryService.loadAllNames(any(), any())).thenReturn(Arrays.asList(testNameEntry, anotherTestNameEntry));

        mockMvc.perform(get("/v1/names?indexed=true"))
               .andExpect(jsonPath("$", hasSize(1)))
               .andExpect(jsonPath("$[0].name", is("test-entry")))
               .andExpect(status().isOk());
    }

    @Test
    public void test_get_all_names_filtered_by_is_submitted_by() throws Exception {
        testNameEntry.setSubmittedBy("test");
        NameEntry secondEntry = new NameEntry("secondEntry");
        secondEntry.setSubmittedBy("Not Available");
        when(entryService.loadAllNames(any(), any())).thenReturn(Arrays.asList(testNameEntry, secondEntry));

        mockMvc.perform(get("/v1/names?submittedBy=test"))
               .andExpect(jsonPath("$", hasSize(1)))
               .andExpect(jsonPath("$[0].name", is("test-entry")))
               .andExpect(status().isOk());
    }


    @Test
    public void test_get_a_name() throws Exception {
        when(entryService.loadName("test-entry")).thenReturn(testNameEntry);
        mockMvc.perform(get("/v1/names/test-entry"))
               .andExpect(jsonPath("$.name", is("test-entry")))
               .andExpect(status().isOk());
    }

    @Test
    public void test_get_all_names() throws Exception {
        when(entryService.loadAllNames()).thenReturn(Arrays.asList(testNameEntry, anotherTestNameEntry));
        mockMvc.perform(get("/v1/names?all=true"))
               .andExpect(jsonPath("$").isArray())
               .andExpect(status().isOk());

        verify(entryService).loadAllNames();
    }


    @Test
    public void test_get_name_count() throws Exception {
        when(entryService.getNameCount()).thenReturn(Long.valueOf(2));
        mockMvc.perform(get("/v1/names/meta/?count=true"))
               .andExpect(jsonPath("$.count", is("2")))
               .andExpect(status().isOk());

        verify(entryService).getNameCount();
    }

    @Test
    public void test_name_count_count_not_specified() throws Exception {
        when(entryService.getNameCount()).thenReturn(Long.valueOf(2));
        mockMvc.perform(get("/v1/names/meta/?count=false"))
               .andExpect(jsonPath("$.count").doesNotExist())
               .andExpect(status().isNoContent());

        verify(entryService, never()).getNameCount();
    }

    @Test
    public void test_get_a_name_not_found_in_db() throws Exception {
        when(entryService.loadName("test")).thenReturn(null);

        mockMvc.perform(get("/v1/names/test"))
               .andExpect(status().isBadRequest());
    }

    @Test
    public void test_get_all_a_name_that_has_duplicates() throws Exception {
        DuplicateNameEntry duplicateNameEntry = new DuplicateNameEntry(testNameEntry);
        when(entryService.loadName("test")).thenReturn(testNameEntry);
        when(entryService.loadNameDuplicates("test")).thenReturn(Arrays.asList(duplicateNameEntry));
        mockMvc.perform(get("/v1/names/test?duplicates=true"))
               .andExpect(jsonPath("$.duplicates", hasSize(1)))
               .andExpect(jsonPath("$.mainEntry.name", is("test-entry")))
               .andExpect(status().isOk());
    }


    @Test
    public void test_add_name_via_post_request() throws Exception {
        String requestJson = new ObjectMapper().writeValueAsString(testNameEntry);
        mockMvc.perform(post("/v1/names")
                                .contentType(MediaType.parseMediaType("application/json; charset=UTF-8"))
                                .content(requestJson))
               .andExpect(jsonPath("$.message", IsNot.not(nullValue())))
               .andExpect(status().isCreated());

        verify(entryService).insertTakingCareOfDuplicates(any(NameEntry.class));
    }

    @Test
    public void test_add_name_via_post_request_make_sure_state_is_new() throws Exception {
        testNameEntry.setState(State.NEW);
        String requestJson = new ObjectMapper().writeValueAsString(testNameEntry);
        mockMvc.perform(post("/v1/names")
                                .contentType(MediaType.parseMediaType("application/json; charset=UTF-8"))
                                .content(requestJson))
               .andExpect(jsonPath("$.message", IsNot.not(nullValue())))
               .andExpect(status().isCreated());

        verify(entryService).insertTakingCareOfDuplicates(any(NameEntry.class));
    }

    @Test
    public void test_add_name_via_post_request_when_state_is_null_it_will_be_new() throws Exception {
        testNameEntry.setState(null);
        ArgumentCaptor<NameEntry> argumentCaptor = ArgumentCaptor.forClass(NameEntry.class);
        String requestJson = new ObjectMapper().writeValueAsString(testNameEntry);
        mockMvc.perform(post("/v1/names")
                                .contentType(MediaType.parseMediaType("application/json; charset=UTF-8"))
                                .content(requestJson))
               .andExpect(jsonPath("$.message", IsNot.not(nullValue())))
               .andExpect(status().isCreated());

        verify(entryService).insertTakingCareOfDuplicates(argumentCaptor.capture());
        final NameEntry value = argumentCaptor.getValue();
        assertThat(value.getState().toString(), is("NEW"));
    }

    @Test
    public void test_add_name_via_post_request_when_state_is_not_null_expect_exception() throws Exception {
        testNameEntry.setState(State.PUBLISHED);
        String requestJson = new ObjectMapper().writeValueAsString(testNameEntry);
        mockMvc.perform(post("/v1/names")
                                .contentType(MediaType.parseMediaType("application/json; charset=UTF-8"))
                                .content(requestJson))
               .andExpect(jsonPath("$.message", IsNot.not(nullValue())))
               .andExpect(status().isBadRequest());
    }

    @Test
    public void test_add_name_via_get_but_faulty_request() throws Exception {
        String requestJson = new ObjectMapper().writeValueAsString(anotherTestNameEntry);
        mockMvc.perform(post("/v1/names")
                                .contentType(MediaType.parseMediaType("application/json; charset=UTF-8"))
                                .content(requestJson))
               .andExpect(status().isBadRequest());
    }

    @Test
    public void test_modifying_name_via_put_request_but_faulty_request() throws Exception {
        String requestJson = new ObjectMapper().writeValueAsString(anotherTestNameEntry);
        mockMvc.perform(put("/v1/names/jaja")
                                .contentType(MediaType.parseMediaType("application/json; charset=UTF-8"))
                                .content(requestJson))
               .andExpect(status().isBadRequest());
    }

    @Test
    public void test_modifying_name_via_put_request_but_name_is_different_from_json_sent() throws Exception {
        String requestJson = new ObjectMapper().writeValueAsString(testNameEntry);
        mockMvc.perform(put("/v1/names/jaja")
                                .contentType(MediaType.parseMediaType("application/json; charset=UTF-8"))
                                .content(requestJson))
               .andExpect(jsonPath("$.error", is(true)))
               .andExpect(status().isInternalServerError());
    }


    @Test
    public void test_modifying_name_via_put_request_but_name_to_update_not_in_db() throws Exception {
        String requestJson = new ObjectMapper().writeValueAsString(testNameEntry);
        mockMvc.perform(put("/v1/names/test")
                                .contentType(MediaType.parseMediaType("application/json; charset=UTF-8"))
                                .content(requestJson))
               .andExpect(jsonPath("$.error", is(true)))
               .andExpect(status().isInternalServerError());
    }

    @Test
    public void test_modifying_name_via_put_request() throws Exception {
        NameEntry nameEntry = mock(NameEntry.class);
        when(entryService.loadName("test")).thenReturn(nameEntry);
        String requestJson = new ObjectMapper().writeValueAsString(testNameEntry);
        mockMvc.perform(put("/v1/names/test")
                                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                                .content(requestJson))
               .andExpect(status().isCreated());

        //TODO revisit and see if argument captor can be put to use here
        verify(entryService).updateName(isA(NameEntry.class), isA(NameEntry.class));
    }

    @Test
    public void test_uploading_vai_spreadsheet() throws Exception {

        ImportStatus importStatus = mock(ImportStatus.class);
        when(importStatus.hasErrors()).thenReturn(false);
        when(importerInterface.importFile(any())).thenReturn(importStatus);
        MockMultipartFile spreadsheet = new MockMultipartFile("nameFiles", "filename.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "some spreadsheet".getBytes());
        mockMvc.perform(MockMvcRequestBuilders.fileUpload("/v1/names/upload").file(spreadsheet))
               .andExpect(status().isAccepted())
               .andExpect(jsonPath("$.message", IsNot.not(nullValue())));

    }

    @Test
    public void test_batch_upload() throws Exception {
        NameEntry[] nameEntries = new NameEntry[2];
        nameEntries[0] = new NameEntry("test");
        nameEntries[1] = new NameEntry("anothertest");
        String requestJson = new ObjectMapper().writeValueAsString(nameEntries);
        mockMvc.perform(post("/v1/names/batch")
                                .contentType(MediaType.parseMediaType("application/json; charset=UTF-8"))
                                .content(requestJson))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.message", IsNot.not(nullValue())));

        verify(entryService, times(1)).bulkInsertTakingCareOfDuplicates(anyListOf(NameEntry.class));
    }

    @Test
    public void test_batch_upload_with_faulty_request() throws Exception {
        NameEntry[] nameEntries = new NameEntry[0];
        String requestJson = new ObjectMapper().writeValueAsString(nameEntries);
        mockMvc.perform(post("/v1/names/batch")
                                .contentType(MediaType.parseMediaType("application/json; charset=UTF-8"))
                                .content(requestJson))
               .andExpect(status().isBadRequest());

        verifyZeroInteractions(entryService);
    }

    @Test
    public void test_deleting_all_names() throws Exception {
        mockMvc.perform(delete("/v1/names")
                                .contentType(MediaType.parseMediaType("application/json; charset=UTF-8")))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.message", IsNot.not(nullValue())));

        verify(entryService).deleteAllAndDuplicates();
    }

    @Test @Ignore
    public void test_deleting_a_name() throws Exception {
        final ArgumentCaptor<NameDeletedEvent> argumentCaptor = ArgumentCaptor
                .forClass(NameDeletedEvent.class);
        when(entryService.loadName("test")).thenReturn(testNameEntry);
        mockMvc.perform(delete("/v1/names/test")
                                .contentType(MediaType.parseMediaType("application/json; charset=UTF-8")))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.message", IsNot.not(nullValue())));

        verify(eventPubService, times(1)).publish(argumentCaptor.capture());
        final NameDeletedEvent nameDeletedEvent = argumentCaptor.getValue();
        assertThat("test", is(nameDeletedEvent.getName()));
    }



    @Test
    public void test_get_names_with_feedback() throws Exception {
        when(entryService.loadName("test")).thenReturn(testNameEntry);
        mockMvc.perform(get("/v1/names/{name}?feedback=true", "test"))
               .andExpect(status().isOk()).andExpect(jsonPath("$.feedback", notNullValue()));
    }

    @Test
    public void test_get_names_without_feedback() throws Exception {
        when(entryService.loadName("test")).thenReturn(testNameEntry);
        mockMvc.perform(get("/v1/names/{name}?feedback=false", "test"))
               .andExpect(status().isOk()).andExpect(jsonPath("$.feedback").doesNotExist());
    }

}