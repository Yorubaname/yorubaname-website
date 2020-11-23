package org.oruko.dictionary.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.oruko.dictionary.model.GeoLocation;
import org.oruko.dictionary.model.SuggestedName;
import org.oruko.dictionary.model.repository.SuggestedNameRepository;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests {@link SuggestionApi}
 * @author Dadepo Aderemi.
 */
@RunWith(MockitoJUnitRunner.class)
public class SuggestionApiTest extends AbstractApiTest {

    @InjectMocks
    SuggestionApi suggestionApi;

    @Mock
    SuggestedNameRepository suggestedNameRepository;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(suggestionApi).setHandlerExceptionResolvers(
                createExceptionResolver()).build();
    }


    @Test
    public void testGetSuggestedMetaData_count() throws Exception {
        when(suggestedNameRepository.count()).thenReturn(2L);
        mockMvc.perform(get("/v1/suggestions/meta"))
               .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
               .andExpect(jsonPath("$.totalSuggestedNames",is(2)))
               .andExpect(status().isOk());

    }

    @Test
    public void testSuggestName() throws Exception {
        SuggestedName suggestedName = new SuggestedName("test", "this is a test",
                                                        Collections.singletonList(new GeoLocation("ABEOKUTA", "NWY")),
                                                        "test@email.com");
        String requestJson = new ObjectMapper().writeValueAsString(suggestedName);
        mockMvc.perform(post("/v1/suggestions")
                                .content(requestJson)
                                .contentType(MediaType.parseMediaType("application/json; charset=UTF-8")))
               .andExpect(status().isCreated());

        verify(suggestedNameRepository).save(isA(SuggestedName.class));
    }


    @Test
    public void testSuggestName_invalid_email() throws Exception {
        SuggestedName suggestedName = new SuggestedName("test", "this is a test",
                                                        Collections.singletonList(new GeoLocation("ABEOKUTA", "NWY")),
                                                        "testemail.com");
        String requestJson = new ObjectMapper().writeValueAsString(suggestedName);
        mockMvc.perform(post("/v1/suggestions")
                                .content(requestJson)
                                .contentType(MediaType.parseMediaType("application/json; charset=UTF-8")))
               .andExpect(status().isBadRequest());

        verify(suggestedNameRepository, never()).save(isA(SuggestedName.class));
    }

    @Test
    public void testSuggestName_invalid_name() throws Exception {
        SuggestedName suggestedName = new SuggestedName("", "this is a test",
                                                        Collections.singletonList(new GeoLocation("ABEOKUTA", "NWY")),
                                                        "test@email.com");
        String requestJson = new ObjectMapper().writeValueAsString(suggestedName);
        mockMvc.perform(post("/v1/suggestions")
                                .content(requestJson)
                                .contentType(MediaType.parseMediaType("application/json; charset=UTF-8")))
               .andExpect(status().isBadRequest());

        verify(suggestedNameRepository, never()).save(isA(SuggestedName.class));
    }

    @Test
    public void testGetAllSuggestedNames() throws Exception {
        mockMvc.perform(get("/v1/suggestions"))
               .andExpect(status().isOk());
        verify(suggestedNameRepository).findAll();
    }

    @Test
    public void testDeleteSuggestedName() throws Exception {
        final Long suggestedNameId = 1L;
        SuggestedName suggestedName = mock(SuggestedName.class);
        when(suggestedNameRepository.findById(suggestedNameId)).thenReturn(Optional.of(suggestedName));
        mockMvc.perform(delete("/v1/suggestions/{name}", suggestedNameId))
               .andExpect(status().isNoContent());
        verify(suggestedNameRepository).delete(isA(SuggestedName.class));
    }

    @Test
    public void testDeleteSuggestedName_name_not_found() throws Exception {
        final Long suggestedNameId = 1L;
        mockMvc.perform(delete("/v1/suggestions/{id}", suggestedNameId))
               .andExpect(status().isBadRequest());
        verify(suggestedNameRepository, never()).delete(isA(SuggestedName.class));
    }

    @Test
    public void testDeleteAllSuggested() throws Exception {
        mockMvc.perform(delete("/v1/suggestions"))
               .andExpect(status().isOk());
        verify(suggestedNameRepository, times(1)).deleteAll();
    }
}