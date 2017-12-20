package org.oruko.dictionary.elasticsearch;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.oruko.dictionary.model.NameEntry;
import org.oruko.dictionary.search.api.SearchService;

import java.io.IOException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.test.ElasticsearchIntegrationTest.ClusterScope;

/**

 *
 * Created by Dadepo Aderemi.
 */
@ClusterScope(scope = ElasticsearchIntegrationTest.Scope.TEST)
public class ElasticSearchServiceTest extends ElasticsearchIntegrationTest {
    private String dictionary = "dictionary";
    private ESConfig esConfig;
    SearchService searchService;

    private class TestNode implements org.elasticsearch.node.Node {

        @Override
        public Settings settings() {
            return null;
        }

        @Override
        public Client client() {
            return org.elasticsearch.test.ElasticsearchIntegrationTest.client();
        }

        @Override
        public org.elasticsearch.node.Node start() {
            return null;
        }

        @Override
        public org.elasticsearch.node.Node stop() {
            return null;
        }

        @Override
        public void close() {

        }

        @Override
        public boolean isClosed() {
            return false;
        }
    }

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

        searchService = new ElasticSearchService(new TestNode(), esConfig);
    }

    @Test
    public void testGetByName() throws IOException {
        XContentBuilder lagbaja = jsonBuilder().startObject()
                                                     .field("name", "jamo")
                                                     .endObject();
        index(esConfig.getIndexName(), esConfig.getDocumentType(), lagbaja);

        flushAndRefresh();

        NameEntry jamo = searchService.getByName("jamo");
        assertEquals("jamo", jamo.getName());
    }

}
