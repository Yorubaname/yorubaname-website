package org.oruko.dictionary.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.IsNot;
import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.runners.*;
import org.oruko.dictionary.importer.ImportStatus;
import org.oruko.dictionary.importer.ImporterInterface;
import org.oruko.dictionary.model.DuplicateNameEntry;
import org.oruko.dictionary.model.GeoLocation;
import org.oruko.dictionary.model.NameDto;
import org.oruko.dictionary.model.NameEntry;
import org.oruko.dictionary.model.SuggestedName;
import org.oruko.dictionary.web.NameEntryService;
import org.oruko.dictionary.web.exception.ApiExceptionHandler;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ExceptionHandlerMethodResolver;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.isA;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test for {@link org.oruko.dictionary.web.rest.NameApi}
 */
@RunWith(MockitoJUnitRunner.class)
public class NameApiTest {

    @InjectMocks
    NameApi nameApi;

    @Mock
    View mockView;

    @Mock
    private NameEntryService entryService;

    @Mock
    private ImporterInterface importerInterface;

    MockMvc mockMvc;

    NameEntry testNameEntry;
    NameEntry faultNameEntry;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(nameApi).setHandlerExceptionResolvers(createExceptionResolver()).build();
        testNameEntry = new NameEntry("test");
        testNameEntry.setMeaning("test_meaning");
        testNameEntry.setExtendedMeaning("test_extended_meaning");

        faultNameEntry = new NameEntry();
    }

    @Test
    public void test_get_all_names_via_get() throws Exception {
        NameEntry firstNameEntry = mock(NameEntry.class);
        NameEntry secondNameEntry = mock(NameEntry.class);
        when(firstNameEntry.toNameDto()).thenReturn(new NameDto("firstname"));
        when(secondNameEntry.toNameDto()).thenReturn(new NameDto("secondname"));
        when(entryService.loadAllNames(any(), any())).thenReturn(Arrays.asList(firstNameEntry, secondNameEntry));
             mockMvc.perform(get("/v1/names"))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].name", is("firstname")))
                    .andExpect(jsonPath("$[1].name", is("secondname")))
                    .andExpect(status().isOk());
    }

    @Test
    public void test_get_all_names_filtered_by_is_indexed() throws Exception {
        NameEntry indexedEntry = mock(NameEntry.class);
        NameEntry notIndexedEntry = mock(NameEntry.class);

        NameDto indexedDto = new NameDto("indexed");
        NameDto notIndexedDto = new NameDto("not-indexed");

        ReflectionTestUtils.setField(indexedDto, "isIndexed", true);
        ReflectionTestUtils.setField(notIndexedDto, "isIndexed", false);

        when(indexedEntry.toNameDto()).thenReturn(indexedDto);
        when(notIndexedEntry.toNameDto()).thenReturn(notIndexedDto);

        when(entryService.loadAllNames(any(), any())).thenReturn(Arrays.asList(indexedEntry, notIndexedEntry));

        mockMvc.perform(get("/v1/names?indexed=true"))
               .andExpect(jsonPath("$", hasSize(1)))
               .andExpect(jsonPath("$[0].name", is("indexed")))
               .andExpect(status().isOk());
    }

    @Test
    public void test_get_all_names_filtered_by_is_submitted_by() throws Exception {
        NameEntry firstEntry = mock(NameEntry.class);
        NameEntry secondEntry = mock(NameEntry.class);

        NameDto firstDto = new NameDto("first-entry");
        NameDto secondDto = new NameDto("second-entry");

        ReflectionTestUtils.setField(firstDto, "submittedBy", "test");
        ReflectionTestUtils.setField(secondDto, "submittedBy", "Not Available");

        when(firstEntry.toNameDto()).thenReturn(firstDto);
        when(secondEntry.toNameDto()).thenReturn(secondDto);

        when(entryService.loadAllNames(any(), any())).thenReturn(Arrays.asList(firstEntry, secondEntry));

        mockMvc.perform(get("/v1/names?submittedBy=test"))
               .andExpect(jsonPath("$", hasSize(1)))
               .andExpect(jsonPath("$[0].name", is("first-entry")))
               .andExpect(status().isOk());
    }


    @Test
    public void test_get_a_name() throws Exception {
        NameEntry nameEntry = mock(NameEntry.class);
        when(entryService.loadName("test")).thenReturn(nameEntry);
        when(nameEntry.toNameDto()).thenReturn(new NameDto("test"));

        mockMvc.perform(get("/v1/names/test"))
               .andExpect(jsonPath("$.name", is("test")))
               .andExpect(status().isOk());
    }

    @Test
    public void test_get_a_name_not_found_in_db() throws Exception {
        when(entryService.loadName("test")).thenReturn(null);

        mockMvc.perform(get("/v1/names/test"))
               .andExpect(status().isBadRequest());
    }

    @Test
    public void test_get_all_a_name_that_has_duplicates() throws Exception {
        NameEntry nameEntry = mock(NameEntry.class);
        DuplicateNameEntry duplicateNameEntry = new DuplicateNameEntry(nameEntry);

        when(entryService.loadName("test")).thenReturn(nameEntry);
        when(entryService.loadNameDuplicates("test")).thenReturn(Arrays.asList(duplicateNameEntry));
        when(nameEntry.toNameDto()).thenReturn(new NameDto("test"));

        mockMvc.perform(get("/v1/names/test?duplicates=true"))
               .andExpect(jsonPath("$.duplicates", hasSize(1)))
               .andExpect(jsonPath("$.mainEntry.name", is("test")))
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
    public void test_add_name_via_get_but_faulty_request() throws Exception {
        String requestJson = new ObjectMapper().writeValueAsString(faultNameEntry);
        mockMvc.perform(post("/v1/names")
                                .contentType(MediaType.parseMediaType("application/json; charset=UTF-8"))
                                .content(requestJson))
               .andExpect(status().isBadRequest());
    }

    @Test
    public void test_modifying_name_via_put_request_but_faulty_request() throws Exception {
        String requestJson = new ObjectMapper().writeValueAsString(faultNameEntry);
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
                                .contentType(MediaType.parseMediaType("application/json; charset=UTF-8"))
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

        verify(entryService, times(2)).insertTakingCareOfDuplicates(any(NameEntry.class));
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

    @Test
    public void test_deleting_a_name() throws Exception {
        mockMvc.perform(delete("/v1/names/test")
                                .contentType(MediaType.parseMediaType("application/json; charset=UTF-8")))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.message", IsNot.not(nullValue())));

        verify(entryService).deleteNameEntryAndDuplicates("test");
    }

    @Test
    public void test_suggest_name() throws Exception {
        SuggestedName suggestedName = new SuggestedName("test","this is a test",
                                                        new GeoLocation("ABEOKUTA", "NWY"),
                                                        "test@email.com");
        String requestJson = new ObjectMapper().writeValueAsString(suggestedName);
        mockMvc.perform(post("/v1/suggest")
                                .content(requestJson)
                                .contentType(MediaType.parseMediaType("application/json; charset=UTF-8")))
               .andExpect(status().isCreated());

        verify(entryService).addSuggestedName(isA(SuggestedName.class));
    }


    @Test
    public void test_suggest_name_invalid_email() throws Exception {
        SuggestedName suggestedName = new SuggestedName("test","this is a test",
                                                        new GeoLocation("ABEOKUTA", "NWY"),
                                                        "@email.com");
        String requestJson = new ObjectMapper().writeValueAsString(suggestedName);
        mockMvc.perform(post("/v1/suggest")
                                .content(requestJson)
                                .contentType(MediaType.parseMediaType("application/json; charset=UTF-8")))
               .andExpect(status().isBadRequest());

    }

    @Test
    public void test_add_feedback() throws Exception {
        String testName = "lagbaja";

        NameEntry nameEntry = mock(NameEntry.class);

        when(entryService.loadName(testName)).thenReturn(nameEntry);

        Map<String, String> feedbackMap = new HashMap<>();
        String testFeedback = "feedback for testing";
        feedbackMap.put("feedback", testFeedback);

        String requestJson = new ObjectMapper().writeValueAsString(feedbackMap);

        mockMvc.perform(post("/v1/{name}/feedback", testName)
                                .content(requestJson)
                                .contentType(MediaType.parseMediaType("application/json; charset=UTF-8")))
               .andExpect(status().isCreated());
    }

    @Test
    public void test_add_feedback_name_not_in_system() throws Exception{
        String testName = "lagbaja";

        when(entryService.loadName(testName)).thenReturn(null); // test condition

        Map<String, String> feedbackMap = new HashMap<>();
        String testFeedback = "feedback for testing";
        feedbackMap.put("feedback", testFeedback);

        String requestJson = new ObjectMapper().writeValueAsString(feedbackMap);

        mockMvc.perform(post("/v1/{name}/feedback", testName)
                                .content(requestJson)
                                .contentType(MediaType.parseMediaType("application/json; charset=UTF-8")))
               .andExpect(status().isBadRequest()).andExpect(jsonPath("$.error", is(true)));
    }

    @Test
    public void test_add_feedback_but_feedback_is_empty() throws Exception{
        String testName = "lagbaja";

        NameEntry nameEntry = mock(NameEntry.class);
        when(entryService.loadName(testName)).thenReturn(nameEntry);

        Map<String, String> feedbackMap = new HashMap<>();
        String testFeedback = ""; // test condition
        feedbackMap.put("feedback", testFeedback);

        String requestJson = new ObjectMapper().writeValueAsString(feedbackMap);

        mockMvc.perform(post("/v1/{name}/feedback", testName)
                                .content(requestJson)
                                .contentType(MediaType.parseMediaType("application/json; charset=UTF-8")))
               .andExpect(status().isBadRequest()).andExpect(jsonPath("$.error", is(true)));
    }

    @Test
    public void test_get_names_with_feedback() throws Exception {
        NameEntry nameEntry = mock(NameEntry.class);
        when(entryService.loadName("test")).thenReturn(nameEntry);
        when(nameEntry.toNameDto()).thenReturn(new NameDto("test"));

        mockMvc.perform(get("/v1/names/{name}?feedback=true", "test"))
               .andExpect(status().isOk()).andExpect(jsonPath("$.feedback", notNullValue()));
    }

    @Test
    public void test_get_names_without_feedback() throws Exception {
        NameEntry nameEntry = mock(NameEntry.class);
        when(entryService.loadName("test")).thenReturn(nameEntry);
        when(nameEntry.toNameDto()).thenReturn(new NameDto("test"));

        mockMvc.perform(get("/v1/names/{name}?feedback=false", "test"))
               .andExpect(status().isOk()).andExpect(jsonPath("$.feedback", CoreMatchers.nullValue()));
    }

    @Test
    public void test_load_suggested_name() throws Exception {

        mockMvc.perform(get("/v1/suggest"))
               .andExpect(status().isOk());
        verify(entryService).loadAllSuggestedNames();
    }

        // ==================================================== Helpers ====================================================

    //TODO seems to be doing nothing. Either remove or make it work!
    private ExceptionHandlerExceptionResolver createExceptionResolver() {
        ExceptionHandlerExceptionResolver exceptionResolver = new ExceptionHandlerExceptionResolver() {
            protected ServletInvocableHandlerMethod getExceptionHandlerMethod(HandlerMethod handlerMethod, Exception exception) {
                Method method = new ExceptionHandlerMethodResolver(ApiExceptionHandler.class).resolveMethod(exception);
                if (method == null) {
                    return null;
                }
                return new ServletInvocableHandlerMethod(new ApiExceptionHandler(), method);
            }
        };
        exceptionResolver.afterPropertiesSet();
        return exceptionResolver;
    }


}