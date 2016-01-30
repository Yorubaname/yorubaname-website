package org.oruko.dictionary.elasticsearch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.node.Node;
import org.elasticsearch.search.SearchHit;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;

import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;

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

    /**
     * Public constructor for {@link ElasticSearchService}
     *
     * @param node the elastic search Node
     * @param esConfig the elastic search config
     */
    @Autowired
    public ElasticSearchService(Node node, ESConfig esConfig) {
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
    public Set<Map<String, Object>> search(String searchTerm) {
        /**
         * 1. First do a exact search. If found return result. If not go to 2.
         * 2. Do a search with ascii-folding. If found return result, if not go to 3
         * 3. Do a prefix search. If found, return result, if not go to 4
         * 4. Do a search based on partial match. Irrespective of outcome, proceed to 4
         *    4a - Using nGram?
         *    4b - ?
         * 5. Do a full text search against other variants. Irrespective of outcome, proceed to 6
         * 6. Do a full text search against meaning. Irrespective of outcome, proceed to 7
         * 7. Do a full text search against extendedMeaning
         */

        final Set<Map<String, Object>> result = new LinkedHashSet<>();

        // 1. exact search
        SearchResponse searchResponse = exactSearchByName(searchTerm);
        if (searchResponse.getHits().getHits().length >= 1) {
            Stream.of(searchResponse.getHits().getHits()).forEach(hit -> {
                result.add(hit.getSource());
            });

            if (result.size() == 1) {
                return result;
            }
        }

        //2. Do a search with ascii-folding
        searchResponse = exactSearchByNameAsciiFolded(searchTerm);
        if (searchResponse.getHits().getHits().length >= 1) {
            Stream.of(searchResponse.getHits().getHits()).forEach(hit -> {
                result.add(hit.getSource());
            });

            if (result.size() == 1) {
                return result;
            }
        }


        //3. Do a prefix search
        searchResponse = prefixFilterSearch(searchTerm, false);
        if (searchResponse.getHits().getHits().length >= 1) {
            Stream.of(searchResponse.getHits().getHits()).forEach(hit -> {
                result.add(hit.getSource());
            });

            return result;
        }

        /**
         * Does a full text search on
         * name,
         * meaning,
         * extendedMeaning
         * variants
         * TODO Should revisit
         */
        MultiMatchQueryBuilder searchSpec = QueryBuilders.multiMatchQuery(searchTerm,
                                                                          "name.autocomplete",
                                                                          "meaning",
                                                                          "extendedMeaning",
                                                                          "variants");

        SearchResponse tempSearchAll = client.prepareSearch(esConfig.getIndexName())
                                             .setQuery(searchSpec)
                                             .setSize(20)
                                             .execute()
                                             .actionGet();

        Stream.of(tempSearchAll.getHits().getHits()).forEach(hit -> {
            result.add(hit.getSource());
        });

        return result;
    }


    public Set<Map<String, Object>> listByAlphabet(String alphabetQuery) {
        final Set<Map<String, Object>> result = new LinkedHashSet<>();

        final SearchResponse searchResponse = prefixFilterSearch(alphabetQuery, true);
        final SearchHit[] hits = searchResponse.getHits().getHits();
        final List<SearchHit> searchHits = Arrays.asList(hits);

        Collections.reverse(searchHits);
        searchHits.forEach(hit -> {
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
     * Add a {@link org.oruko.dictionary.model.NameEntry} into ElasticSearch index
     *
     * @param entry the {@link org.oruko.dictionary.model.NameEntry} to index
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

            return new IndexOperationStatus(true, name + " indexed successfully");
        } catch (JsonProcessingException e) {
            logger.info("Failed to parse NameEntry into Json", e);
            return new IndexOperationStatus(true, "Failed to parse NameEntry into Json");
        }
    }


    public IndexOperationStatus bulkIndexName(List<NameEntry> entries) {

        if (entries.size() == 0) {
            return new IndexOperationStatus(false, "Cannot index an empty list");
        }

        if (!isElasticSearchNodeAvailable()) {
            return new IndexOperationStatus(false,
                                            "Index attempt unsuccessful. You do not have an elasticsearch node running");
        }

        BulkRequestBuilder bulkRequest = client.prepareBulk();
        entries.forEach(entry -> {
            try {
                String entryAsJson = mapper.writeValueAsString(entry);
                String name = entry.getName();
                IndexRequestBuilder request = client.prepareIndex(esConfig.getIndexName(),
                                                                  esConfig.getDocumentType(),
                                                                  name.toLowerCase())
                                                    .setSource(entryAsJson);
                bulkRequest.add(request);
            } catch (JsonProcessingException e) {
                logger.debug("Error while building bulk indexing operation", e);
            }
        });

        BulkResponse bulkResponse = bulkRequest.execute().actionGet();
        if (bulkResponse.hasFailures()) {
            return new IndexOperationStatus(false, bulkResponse.buildFailureMessage());
        }

        return new IndexOperationStatus(true, "Bulk indexing successfully. Indexed the following names "
                + String.join(",", entries.stream().map(entry -> entry.getName()).collect(Collectors.toList())));
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

        DeleteResponse response = deleteName(name);
        return new IndexOperationStatus(response.isFound(), name + " deleted from index");
    }

    public IndexOperationStatus bulkDeleteFromIndex(List<String> entries) {
        if (entries.size() == 0) {
            return new IndexOperationStatus(false, "Cannot index an empty list");
        }

        if (!isElasticSearchNodeAvailable()) {
            return new IndexOperationStatus(false,
                                            "Delete unsuccessful. You do not have an elasticsearch node running");
        }

        BulkRequestBuilder bulkRequest = client.prepareBulk();

        entries.forEach(entry -> {
            DeleteRequestBuilder request = client.prepareDelete(esConfig.getIndexName(),
                                                                esConfig.getDocumentType(),
                                                                entry.toLowerCase());
            bulkRequest.add(request);
        });

        BulkResponse bulkResponse = bulkRequest.execute().actionGet();
        if (bulkResponse.hasFailures()) {
            return new IndexOperationStatus(false, bulkResponse.buildFailureMessage());
        }

        return new IndexOperationStatus(true, "Bulk deleting successfully. "
                + "Removed the following names from search index "
                + String.join(",", entries));
    }


    //=====================================Helpers=========================================================//

    private DeleteResponse deleteName(String name) {
        return client
                .prepareDelete(esConfig.getIndexName(), esConfig.getDocumentType(), name.toLowerCase())
                .execute()
                .actionGet();
    }

    //TODO revisit. Omo returns Omowunmi and Owolabi. Ideal this should return just one result
    private SearchResponse exactSearchByName(String nameQuery) {
        return client.prepareSearch(esConfig.getIndexName())
                     .setPostFilter(FilterBuilders.termFilter("name", nameQuery.toLowerCase()))
                     .execute()
                     .actionGet();
    }

    private SearchResponse prefixFilterSearch(String nameQuery, boolean getAll) {
        int resultSet = 20;

        if (getAll) {
            CountResponse countResponse = client.prepareCount(esConfig.getIndexName()).execute().actionGet();
            resultSet = (int) countResponse.getCount();
        }

        return client.prepareSearch(esConfig.getIndexName())
                     .setPostFilter(FilterBuilders.prefixFilter("name", nameQuery.toLowerCase()))
                     .setSize(resultSet)
                     .execute()
                     .actionGet();
    }

    private SearchResponse exactSearchByNameAsciiFolded(String nameQuery) {
        return client.prepareSearch(esConfig.getIndexName())
                     .setQuery(QueryBuilders.matchQuery("name.asciifolded", nameQuery.toLowerCase()))
                     .setSize(20)
                     .execute()
                     .actionGet();
    }

    private SearchResponse partialSearchByName(String nameQuery) {
        return client.prepareSearch(esConfig.getIndexName())
                     .setQuery(QueryBuilders.matchQuery("name.autocomplete", nameQuery.toLowerCase()))
                     .setSize(20)
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

    public long getCount() {
        try {
            CountResponse response = client.prepareCount(esConfig.getIndexName())
                                           .setQuery(matchAllQuery())
                                           .execute()
                                           .actionGet();
            return response.getCount();
        } catch (Exception e) {
            return 0;
        }
    }
}
