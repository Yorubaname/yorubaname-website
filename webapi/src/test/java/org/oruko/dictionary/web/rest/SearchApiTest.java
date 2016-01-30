package org.oruko.dictionary.web.rest;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.oruko.dictionary.elasticsearch.ElasticSearchService;
import org.oruko.dictionary.events.EventPubService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by Dadepo Aderemi.
 */
@RunWith(MockitoJUnitRunner.class)
public class SearchApiTest extends AbstractApiTest {

    @InjectMocks
    SearchApi searchApi;
    MockMvc mockMvc;

    @Mock
    ElasticSearchService searchService;

    @Mock
    EventPubService eventPubService;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(searchApi).setHandlerExceptionResolvers(createExceptionResolver()).build();
    }

    @Test
    public void testSearch() throws Exception {
        mockMvc.perform(get("/v1/search?q=query"))
               .andExpect(status().isOk());
        verify(searchService).search("query");
    }

    @Test
    public void test_auto_complete() throws Exception {
        mockMvc.perform(get("/v1/search/autocomplete?q=query"))
               .andExpect(status().isOk());
        verify(searchService).autocomplete("query");
    }

    @Test
    public void test_auto_complete_with_empty_query() throws Exception {
        mockMvc.perform(get("/v1/search/autocomplete"))
                .andExpect(content().string("[]"))
               .andExpect(status().isOk());
        verifyZeroInteractions(searchService);
    }

    @Test
    public void testFindByName_NameNotFound() throws Exception {
        when(searchService.getByName("query")).thenReturn(Collections.emptyMap());
        mockMvc.perform(get("/v1/search/query"))
               .andExpect(jsonPath("$").isEmpty())
               .andExpect(status().isOk());
    }

    @Test @Ignore
    public void testFindByName() throws Exception {
        Map<String, Object> result = new HashMap<>();
        result.put("test", "test");
        when(searchService.getByName("query")).thenReturn(result);
        mockMvc.perform(get("/v1/search/query"))
               .andExpect(jsonPath("$.test", is("test")))
               .andExpect(status().isOk());

        //verify(eventPubService).publish(any(NameSearchedEvent.class));
    }

}
