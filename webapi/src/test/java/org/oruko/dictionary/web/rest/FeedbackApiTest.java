package org.oruko.dictionary.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.oruko.dictionary.model.NameEntry;
import org.oruko.dictionary.model.NameEntryFeedback;
import org.oruko.dictionary.model.repository.NameEntryFeedbackRepository;
import org.oruko.dictionary.web.NameEntryService;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests {@link FeedbackApi}
 *
 * @author Dadepo Aderemi.
 */
@RunWith(MockitoJUnitRunner.class)
public class FeedbackApiTest extends AbstractApiTest {

    @InjectMocks
    FeedbackApi feedbackApi;

    @Mock
    NameEntryFeedbackRepository feedbackRepository;

    @Mock
    NameEntryService entryService;

    MockMvc mockMvc;

    String testName;
    String testFeedback;
    Map<String, String> feedbackMap;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(feedbackApi).setHandlerExceptionResolvers(
                createExceptionResolver()).build();

        testName =  "lagbaja";
        testFeedback = "feedback for testing";

        feedbackMap = new HashMap<>();
        feedbackMap.put("name", testName);
        feedbackMap.put("feedback", testFeedback);
    }

    @Test
    public void testGetFeedbacks() throws Exception {
        final Sort sort = new Sort(Sort.Direction.DESC, "submittedAt");

        final ArgumentCaptor<Sort> argumentCaptor = ArgumentCaptor.forClass(Sort.class);
        mockMvc.perform(get("/v1/feedbacks")
                                .contentType(MediaType.parseMediaType("application/json; charset=UTF-8")))
               .andExpect(status().isOk());
        verify(feedbackRepository).findAll(argumentCaptor.capture());
        final Sort value = argumentCaptor.getValue();
        assertThat(value.equals(sort), is(true));
    }

    @Test
    public void testGetFeedbacksForName() throws Exception {
        final Sort sort = new Sort(Sort.Direction.DESC, "submittedAt");
        final ArgumentCaptor<Sort> sortCaptor = ArgumentCaptor.forClass(Sort.class);
        mockMvc.perform(get("/v1/feedbacks?name="+ testName)
                                .contentType(MediaType.parseMediaType("application/json; charset=UTF-8")))
               .andExpect(status().isOk());
        verify(feedbackRepository).findByName(eq(testName), sortCaptor.capture());
        final Sort value = sortCaptor.getValue();
        assertThat(value.equals(sort), is(true));
    }

    @Test
    public void testdeleteAllFeedbackForName() throws Exception {
        NameEntry nameEntry = mock(NameEntry.class);
        NameEntryFeedback feedback = mock(NameEntryFeedback.class);
        when(entryService.loadName(testName)).thenReturn(nameEntry);
        final Sort sort = new Sort(Sort.Direction.DESC, "submittedAt");
        when(feedbackRepository.findByName(testName,sort)).thenReturn(Arrays.<NameEntryFeedback>asList(feedback));
        final ArgumentCaptor<Sort> sortCaptor = ArgumentCaptor.forClass(Sort.class);
        mockMvc.perform(delete("/v1/feedbacks?name="+ testName)
                                .contentType(MediaType.parseMediaType("application/json; charset=UTF-8")))
               .andExpect(status().isOk());
        verify(feedbackRepository).findByName(eq(testName), sortCaptor.capture());
        verify(feedbackRepository, times(1)).delete(feedback);
        final Sort value = sortCaptor.getValue();
        assertThat(value.equals(sort), is(true));
    }

    @Test
    public void testdeleteAllFeedbackForName_name_not_found() throws Exception {
        NameEntryFeedback feedback = mock(NameEntryFeedback.class);
        when(entryService.loadName(testName)).thenReturn(null);
        mockMvc.perform(delete("/v1/feedbacks?name="+ testName)
                                .contentType(MediaType.parseMediaType("application/json; charset=UTF-8")))
               .andExpect(status().isBadRequest());

        verify(feedbackRepository, never()).delete(feedback);
    }

    @Test
    public void testDeleteAFeedback() throws Exception {
        NameEntryFeedback feedback = mock(NameEntryFeedback.class);
        when(feedbackRepository.findOne(1L)).thenReturn(feedback);
        mockMvc.perform(delete("/v1/feedbacks/1")
                                .contentType(MediaType.parseMediaType("application/json; charset=UTF-8")))
               .andExpect(status().isOk());

        verify(feedbackRepository).delete(1L);
    }

    @Test
    public void testDeleteAFeedback_no_feedback_for_id() throws Exception {
        when(feedbackRepository.findOne(1L)).thenReturn(null);
        mockMvc.perform(delete("/v1/feedbacks/1")
                                .contentType(MediaType.parseMediaType("application/json; charset=UTF-8")))
               .andExpect(status().isBadRequest());

        verify(feedbackRepository, never()).delete(1L);
    }

    @Test
    public void testDeleteAllFeedback() throws Exception {
        mockMvc.perform(delete("/v1/feedbacks")
                                .contentType(MediaType.parseMediaType("application/json; charset=UTF-8")))
               .andExpect(status().isOk());

        verify(feedbackRepository).deleteAll();
    }

    @Test
    public void testAddFeedback() throws Exception {
        NameEntry nameEntry = mock(NameEntry.class);
        when(entryService.loadName(testName)).thenReturn(nameEntry);
        String requestJson = new ObjectMapper().writeValueAsString(feedbackMap);
        ArgumentCaptor<NameEntryFeedback> argumentCaptor = ArgumentCaptor.forClass(
                NameEntryFeedback.class);

        mockMvc.perform(post("/v1/feedbacks")
                                .content(requestJson)
                                .contentType(MediaType.parseMediaType("application/json; charset=UTF-8")))
               .andExpect(status().isCreated());

        verify(feedbackRepository).save(argumentCaptor.capture());
        final NameEntryFeedback nameEntryValue = argumentCaptor.getValue();
        assertThat(nameEntryValue.getName(), is(testName));
        assertThat(nameEntryValue.getFeedback(), is(testFeedback));
    }

    @Test
    public void test_add_feedback_name_not_in_system() throws Exception{

        when(entryService.loadName(testName)).thenReturn(null); // test condition
        String requestJson = new ObjectMapper().writeValueAsString(feedbackMap);

        mockMvc.perform(post("/v1/feedbacks")
                                .content(requestJson)
                                .contentType(MediaType.parseMediaType("application/json; charset=UTF-8")))
               .andExpect(status().isBadRequest()).andExpect(jsonPath("$.error", is(true)));
    }

    @Test
    public void test_add_feedback_but_feedback_is_empty() throws Exception {

        NameEntry nameEntry = mock(NameEntry.class);
        when(entryService.loadName(testName)).thenReturn(nameEntry);

        Map<String, String> feedbackMap = new HashMap<>();
        String testFeedback = ""; // test condition
        feedbackMap.put("feedback", testFeedback);

        String requestJson = new ObjectMapper().writeValueAsString(feedbackMap);

        mockMvc.perform(post("/v1/feedbacks")
                                .content(requestJson)
                                .contentType(MediaType.parseMediaType("application/json; charset=UTF-8")))
               .andExpect(status().isBadRequest()).andExpect(jsonPath("$.error", is(true)));
    }

}