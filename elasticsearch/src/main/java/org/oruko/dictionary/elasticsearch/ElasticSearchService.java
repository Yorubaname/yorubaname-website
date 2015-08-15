package org.oruko.dictionary.elasticsearch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.node.Node;
import org.elasticsearch.search.SearchHit;
import org.oruko.dictionary.events.EventPubService;
import org.oruko.dictionary.events.NameIndexedEvent;
import org.oruko.dictionary.model.NameEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;

/**
 * Service to interact with ElasticSearch Index
 *
 * @author Dadepo Aderemi.
 */
@Service
public class ElasticSearchService {

    private Logger logger = LoggerFactory.getLogger(ElasticSearchService.class);

    private Node node;
    private Client client;
    private ESConfig esConfig;
    private ResourceLoader resourceLoader;
    private ObjectMapper mapper = new ObjectMapper();
    private EventPubService eventPubService;

    /**
     * Public constructor for {@link ElasticSearchService}
     *
     * @param eventPubService for publishing events
     */
    @Autowired
    public ElasticSearchService(Node node, EventPubService eventPubService, ESConfig esConfig) {
        this.eventPubService = eventPubService;
        this.node = node;
        this.client = node.client();
        this.esConfig = esConfig;
    }

    @Autowired
    public void setResourceLoader(ResourceLoader loader) {
        this.resourceLoader = loader;
    }

    ElasticSearchService() {
    }

    public boolean isElasticSearchNodeAvailable() {
        return !node.isClosed();
    }

    /**
     * For getting an entry from the search index by name
     *
     * @param nameQuery the name
     * @return the nameEntry as a Map or null if name not found
     */
    public Map<String, Object> getByName(String nameQuery) {
        SearchResponse searchResponse = exactSearchByName(nameQuery);

        SearchHit[] hits = searchResponse.getHits().getHits();
        if (hits.length == 1) {
            return hits[0].getSource();
        } else {
            return null;
        }
    }

    /**
     * For searching the index for a name
     *
     * @param searchTerm the search term
     * @return the list of entries found
     */
    public List<Map<String, Object>> search(String searchTerm) {
        /**
         * 1. First do a exact search. If found return result. If not go to 2.
         * 2. Do a search based on partial match. Irrespective of outcome, proceed to 3
         *    2a - Using nGram?
         *    2b - ?
         * 3. Do a full text search against other variants. Irrespective of outcome, proceed to 4
         * 4. Do a full text search against meaning. Irrespective of outcome, proceed to 5
         * 5. Do a full text search against extendedMeaning
         */

        final List<Map<String, Object>> result = new ArrayList();

        // 1. exact search
        SearchResponse searchResponse = exactSearchByName(searchTerm);
        if (searchResponse.getHits().getHits().length >= 1) {
            Stream.of(searchResponse.getHits().getHits()).forEach(hit -> {
                result.add(hit.getSource());
            });

            return result;
        }


        // TODO update to implement search behaviour needed
        SearchResponse tempSearchAll = client.prepareSearch(esConfig.getIndexName())
                                             .setQuery(QueryBuilders.matchAllQuery())
                                             .execute()
                                             .actionGet();

        Stream.of(tempSearchAll.getHits().getHits()).forEach(hit -> {
            result.add(hit.getSource());
        });

        return result;
    }

    /**
     * For getting the list of partial matches for autocomplete
     *
     * @param query the query
     * @return the list of partial matches
     */
    public List<String> autocomplete(String query) {
        final List<String> result = new ArrayList();

        SearchResponse tempSearchAll = partialSearchByName(query);

        Stream.of(tempSearchAll.getHits().getHits()).forEach(hit -> {
            result.add(hit.getSource().get("name").toString());
        });

        return result;
    }


    /**
     * Add a {@link org.oruko.dictionary.model.NameDto} into ElasticSearch index
     *
     * @param entry the {@link org.oruko.dictionary.model.NameDto} to index
     * @return returns true | false depending on if the indexing operation was successful.
     */
    public IndexOperationStatus indexName(NameEntry entry) {

        if (!isElasticSearchNodeAvailable()) {
            return new IndexOperationStatus(false,
                                            "Index attempt unsuccessful. You do not have an elasticsearch node running");
        }

        try {
            String entryAsJson = mapper.writeValueAsString(entry);
            String name = entry.getName();
            client.prepareIndex(esConfig.getIndexName(), esConfig.getDocumentType(), name.toLowerCase())
                  .setSource(entryAsJson)
                  .execute()
                  .actionGet();

            eventPubService.publish(new NameIndexedEvent(name));

            return new IndexOperationStatus(true, name + " indexed successfully");
        } catch (JsonProcessingException e) {
            logger.info("Failed to parse NameEntry into Json", e);
            return new IndexOperationStatus(true, "Failed to parse NameEntry into Json");
        }
    }

    /**
     * Deletes a document by name (which is the id within ElasticSearch index)
     *
     * @param name the name to delete from the index
     * @return true | false depending on if deleting operation was successful
     */
    public IndexOperationStatus deleteFromIndex(String name) {

        if (!isElasticSearchNodeAvailable()) {

            return new IndexOperationStatus(false,
                                            "Delete unsuccessful. You do not have an elasticsearch node running");
        }

        DeleteResponse response = client
                .prepareDelete(esConfig.getIndexName(), esConfig.getDocumentType(), name.toLowerCase())
                .execute()
                .actionGet();

        return new IndexOperationStatus(response.isFound(), name + " deleted from index");
    }


    private SearchResponse exactSearchByName(String nameQuery) {
        return client.prepareSearch(esConfig.getIndexName())
                     .setPostFilter(FilterBuilders.termFilter("name", nameQuery.toLowerCase()))
                     .execute()
                     .actionGet();
    }

    private SearchResponse partialSearchByName(String nameQuery) {
        return client.prepareSearch(esConfig.getIndexName())
                     .setQuery(QueryBuilders.matchQuery("name", nameQuery.toLowerCase()))
                     .execute()
                     .actionGet();
    }

    // On start up, creates an index for the application if one does not
    // already exists and add the custom mapping
    @PostConstruct
    private void buildElasticSearchClient() {
        String mapping = "";
        String indexSettings = "";

        Resource mappingResource = resourceLoader.getResource("classpath:NameEntryElasticSearchMapping.json");
        Resource settingResource = resourceLoader.getResource("classpath:NameEntryElasticSearchSettings.json");

        try {
            mapping = new String(FileCopyUtils.copyToByteArray(mappingResource.getInputStream()));
            indexSettings = new String(FileCopyUtils.copyToByteArray(settingResource.getInputStream()));
        } catch (IOException e) {
            logger.info("Failed to read ES mapping");
        }

        try {
            boolean exists = this.client.admin().indices()
                                        .prepareExists(esConfig.getIndexName())
                                        .execute()
                                        .actionGet()
                                        .isExists();

            if (!exists) {

                CreateIndexResponse createIndexResponse = this.client.admin().indices()
                                                                     .prepareCreate(esConfig.getIndexName())
                                                                     .setSettings(indexSettings)
                                                                     .addMapping(esConfig.getDocumentType(),
                                                                                 mapping).execute()
                                                                     .actionGet();

                logger.info("Created {} and added {} to type {} status was {} at startup",
                            esConfig.getIndexName(), mapping, esConfig.getDocumentType(),
                            createIndexResponse.isAcknowledged());
            }
        } catch (Exception e) {
            logger.error("ElasticSearch not running", e);
        }
    }
}
