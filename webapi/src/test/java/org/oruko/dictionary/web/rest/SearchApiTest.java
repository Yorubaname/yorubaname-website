package org.oruko.dictionary.web.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.oruko.dictionary.events.EventPubService;
import org.oruko.dictionary.service.SearchService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Dadepo Aderemi.
 */
@RunWith(MockitoJUnitRunner.class)
public class SearchApiTest extends AbstractApiTest {

    @InjectMocks
    SearchApi searchApi;
    MockMvc mockMvc;

    @Mock
    SearchService searchService;

    @Mock
    EventPubService eventPubService;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(searchApi).setHandlerExceptionResolvers(createExceptionResolver()).build();
    }


    @Test
    public void testMetadata() throws Exception {
        when(searchService.getSearchableNames()).thenReturn(3);
        mockMvc.perform(get("/v1/search/meta"))
               .andExpect(jsonPath("$.totalPublishedNames", is(3)))
               .andExpect(status().isOk());

        verify(searchService).getSearchableNames();
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
        when(searchService.getByName("query")).thenReturn(null);
        mockMvc.perform(get("/v1/search/query"))
               .andExpect(jsonPath("$").isEmpty())
               .andExpect(status().isOk());
    }


}
