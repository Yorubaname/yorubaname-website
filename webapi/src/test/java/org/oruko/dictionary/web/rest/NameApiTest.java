package org.oruko.dictionary.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.runners.*;
import org.oruko.dictionary.model.NameDto;
import org.oruko.dictionary.model.NameEntry;
import org.oruko.dictionary.model.NameEntryService;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.View;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    MockMvc mockMvc;

    NameEntry testNameEntry;
    NameEntry faultNameEntry;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(nameApi).build();
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
               .andExpect(status().isInternalServerError());
    }

    @Test
    @Ignore
    public void test_get_all_a_name_that_has_duplicates() throws Exception {
        // TODO
    }

    @Test
    public void test_add_name_via_post_request() throws Exception {
        String requestJson = new ObjectMapper().writeValueAsString(testNameEntry);
        mockMvc.perform(post("/v1/names")
                                .contentType(MediaType.parseMediaType("application/json; charset=UTF-8"))
                                .content(requestJson))
               .andExpect(status().isCreated());

        verify(entryService).insertTakingCareOfDuplicates(any(NameEntry.class));
    }

    @Test
    public void test_add_name_via_get_but_faulty_request() throws Exception {
        String requestJson = new ObjectMapper().writeValueAsString(faultNameEntry);
        mockMvc.perform(post("/v1/names")
                                .contentType(MediaType.parseMediaType("application/json; charset=UTF-8"))
                                .content(requestJson))
               .andExpect(status().isInternalServerError());
    }

    @Test
    @Ignore
    public void test_modifying_name_via_put_request() throws Exception {
        // TODO
    }

    @Test
    @Ignore
    public void test_modifying_name_via_put_request_but_faulty_request() throws Exception {
        // TODO
    }

    @Test
    @Ignore
    public void test_uploading_vai_spreadsheet() throws Exception {
        // TODO
    }

    @Test
    @Ignore
    public void test_batch_upload() throws Exception {
        // TODO
    }

    @Test
    @Ignore
    public void test_deleting_a_name() throws Exception {
        // TODO
    }

    @Test
    @Ignore
    public void test_deleting_all_names() throws Exception {
        // TODO
    }

}