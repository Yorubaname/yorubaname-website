package org.oruko.dictionary.elasticsearch;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.*;
import org.oruko.dictionary.events.EventPubService;

import java.io.IOException;
import java.util.Map;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.test.ElasticsearchIntegrationTest.ClusterScope;
import static org.mockito.Mockito.*;

/**
 * Integration test for {@link ElasticSearchService}
 *
 * Created by Dadepo Aderemi.
 */
@ClusterScope(scope = ElasticsearchIntegrationTest.Scope.TEST)
public class ElasticSearchServiceTest extends ElasticsearchIntegrationTest {
    private String dictionary = "dictionary";
    private EventPubService eventPubService;
    private ESConfig esConfig;
    ElasticSearchService elasticSearchService;

    @Before
    public void setup() throws IOException {

        esConfig = new ESConfig();
        esConfig.setDocumentType("nameentry");
        esConfig.setIndexName("dictionary");
        esConfig.setClusterName("yoruba_name_dictionary");
        esConfig.setHostName("localhost");
        esConfig.setPort(9300);

        createIndex(dictionary);

        flushAndRefresh();

        eventPubService = mock(EventPubService.class);
        elasticSearchService = new ElasticSearchService(client(), eventPubService, esConfig);
    }

    @Test
    public void testGetByName() throws IOException {
        XContentBuilder lagbaja = jsonBuilder().startObject()
                                                     .field("name", "jamo")
                                                     .endObject();
        index(esConfig.getIndexName(), esConfig.getDocumentType(), lagbaja);

        flushAndRefresh();

        Map<String, Object> result = elasticSearchService.getByName("jamo");
        assertEquals("jamo", result.get("name"));
    }

}
