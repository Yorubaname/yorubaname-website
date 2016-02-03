package org.oruko.dictionary.auth;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests {@link AuthController}
 * @author Dadepo Aderemi.
 */
@RunWith(MockitoJUnitRunner.class)
public class AuthControllerTest {

    @InjectMocks
    AuthController authController;

    @Mock
    ApiUserRepository apiUserRepository;

    MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }


    @Test
    public void testGetAuthMetaData() throws Exception {
        when(apiUserRepository.count()).thenReturn(2L);
        mockMvc.perform(get("/v1/auth/meta"))
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.totalUsers",is(2)))
               .andExpect(status().isOk());
    }
}