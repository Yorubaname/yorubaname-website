package org.oruko.dictionary.elasticsearch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.common.collect.ImmutableList;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermFilterBuilder;
import org.elasticsearch.search.SearchHit;
import org.oruko.dictionary.events.EventPubService;
import org.oruko.dictionary.events.NameIndexedEvent;
import org.oruko.dictionary.model.NameEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
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

    private Client client;
    private String clusterName;
    private String hostName;
    private String indexName;
    private String documentType;
    private Integer port;
    private ResourceLoader resourceLoader;
    private ObjectMapper mapper = new ObjectMapper();
    private boolean elasticSearchNodeAvailable;

    @Autowired
    private EventPubService eventPubService;

    @Value("${es.clustername:yoruba_name_dictionary}")
    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    @Value("${es.hostname:localhost}")
    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    @Value("${es.portnumber:9300}")
    public void setPort(Integer port) {
        this.port = port;
    }

    @Value("${es.indexname:nameentry}")
    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    @Value("${es.documenttype:nameentry}")
    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    @Autowired
    public void setResourceLoader(ResourceLoader loader) {
        this.resourceLoader = loader;
    }

    ElasticSearchService() {
    }

    public boolean isElasticSearchNodeAvailable() {
        //TODO revisit and examine performance consequence
        ImmutableList<DiscoveryNode> nodes = ((TransportClient) client).connectedNodes();
        if (nodes.isEmpty()) {
            return false;
        }
        return true;
    }


    /**
     * For getting an entry from the search index by name
     * @param nameQuery the name
     * @return the nameEntry as a Map or null if name not found
     */
    public Map<String, Object> getByName(String nameQuery) {
        SearchResponse searchResponse = exactSearchGetByName(nameQuery);

        SearchHit[] hits = searchResponse.getHits().getHits();
        if (hits.length == 1) {
            return hits[0].getSource();
        } else {
            return null;
        }
    }

    /**
     * For searching the index for a name
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
        SearchResponse searchResponse = exactSearchGetByName(searchTerm);
        if (searchResponse.getHits().getHits().length >= 1) {
            Stream.of(searchResponse.getHits().getHits()).forEach(hit -> {
                result.add(hit.getSource());
            });

            return result;
        }


        // TODO update to implement search behaviour needed
        SearchResponse tempSearchAll = client.prepareSearch(indexName)
                                              .setQuery(QueryBuilders.matchAllQuery())
                                              .execute()
                                              .actionGet();

        Stream.of(tempSearchAll.getHits().getHits()).forEach(hit -> {
                result.add(hit.getSource());
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
            client.prepareIndex(indexName, documentType, name.toLowerCase())
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

        DeleteResponse response = client.prepareDelete(indexName, documentType, name.toLowerCase())
                                        .execute()
                                        .actionGet();

        return new IndexOperationStatus(response.isFound(),name + " deleted from index");
    }


    private SearchResponse exactSearchGetByName(String nameQuery) {
        TermFilterBuilder termFilter = FilterBuilders.termFilter("name", nameQuery.toLowerCase());
        return client.prepareSearch(indexName).setPostFilter(termFilter).execute().actionGet();
    }

    // On start up, creates an index for the application if one does not
    // already exists and add the custom mapping
    @PostConstruct
    private void buildElasticSearchClient() {
        String mapping = "";
        Settings settings = ImmutableSettings.settingsBuilder()
                                             .put("cluster.name", clusterName)
                                             .build();

        Resource resource = resourceLoader.getResource("classpath:NameEntryElasticSearchMapping.json");

        try {
            InputStream resourceInputStream = resource.getInputStream();
            mapping = new String(FileCopyUtils.copyToByteArray(resourceInputStream));
        } catch (IOException e) {
            logger.info("Failed to read ES mapping");
        }

        client = new TransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(hostName, port));

        ImmutableList<DiscoveryNode> nodes = ((TransportClient) client).connectedNodes();

        if (nodes.isEmpty()) {
            elasticSearchNodeAvailable = false;
            client.close();
        } else {
            elasticSearchNodeAvailable = true;

            try {
                boolean exists = client.admin().indices().prepareExists(indexName).execute().actionGet().isExists();

                if (!exists) {
                    client.admin().indices()
                          .create(new CreateIndexRequest(indexName)).actionGet();
                }

                PutMappingResponse putMappingResponse = client.admin().indices()
                                                              .preparePutMapping(indexName)
                                                              .setType(documentType)
                                                              .setSource(mapping)
                                                              .execute().actionGet();

                logger.info("Adding {} to type {} in index {} was {} at startup",
                            mapping, documentType, indexName, putMappingResponse.isAcknowledged());
            } catch (Exception e) {
                logger.error("ElasticSearch not running", e);
            }
        }
    }


}
