package org.oruko.dictionary.elasticsearch;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Configuration class for ElasticSearch
 * Created by Dadepo Aderemi.
 */
@Component
public class ESConfig {

    private String clusterName;
    private String hostName;
    private String indexName;
    private String documentType;
    private Integer port;
    private String dataPath;

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

    @Value("${es.documenttype:dictionary}")
    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    @Value("${es.data.path}")
    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }

    public String getDataPath() {
        return dataPath;
    }

    public String getClusterName() {
        return clusterName;
    }

    public String getHostName() {
        return hostName;
    }

    public String getIndexName() {
        return indexName;
    }

    public String getDocumentType() {
        return documentType;
    }

    public Integer getPort() {
        return port;
    }
}
