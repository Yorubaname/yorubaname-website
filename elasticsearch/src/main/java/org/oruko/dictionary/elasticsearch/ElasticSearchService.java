package org.oruko.dictionary.elasticsearch;

import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service to interact with ElasticSearch Index
 * @author Dadepo Aderemi.
 */
@Service
public class ElasticSearchService {

    private Client client;
    private String clusterName;
    private String hostName;
    private Integer port;

    @Value("${es.clustername}")
    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    @Value("${es.hostname}")
    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    @Value("${es.portnumber}")
    public void setPort(Integer port) {
        this.port = port;
    }


    ElasticSearchService() {
        System.out.printf("yo");
    }

    public void doIndex(IndexedNameEntry entry) {
        System.out.println(333);
    }
}
