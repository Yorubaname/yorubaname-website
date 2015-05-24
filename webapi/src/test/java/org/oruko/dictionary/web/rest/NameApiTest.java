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

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(nameApi).build();
        testNameEntry = new NameEntry("test");
        testNameEntry.setMeaning("test_meaning");
        testNameEntry.setExtendedMeaning("test_extended_meaning");
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
    @Ignore
    public void test_get_all_names_filterd_by_is_indexed() throws Exception {
        // TODO
    }

    @Test
    @Ignore
    public void test_get_all_names_filterd_by_is_submitted_by() throws Exception {
        // TODO
    }


    @Test
    @Ignore
    public void test_get_all_a_name() throws Exception {
        // TODO
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

        verify(entryService).insertTakingCareOfDuplicates(anyObject()); // TODO. NameEntry and not any
    }


    @Test
    @Ignore
    public void test_add_duplicate_name_via_post_request() throws Exception {
        // TODO
    }

    @Test
    @Ignore
    public void test_add_name_via_get_but_faulty_request() throws Exception {
        // TODO
    }

    @Test
    @Ignore
    public void test_modofying_name_via_put_request() throws Exception {
        // TODO
    }

    @Test
    @Ignore
    public void test_modofying_name_via_put_request_but_faulty_request() throws Exception {
        // TODO
    }

    @Test
    @Ignore
    public void test_uploading_vai_spreadsheet() throws Exception {
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