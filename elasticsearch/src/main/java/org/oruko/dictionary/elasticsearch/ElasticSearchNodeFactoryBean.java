package org.oruko.dictionary.elasticsearch;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

/**
 * Factory Bean for creating an Elastic Search node
 *
 * Created by Dadepo Aderemi.
 */
@Component
public class ElasticSearchNodeFactoryBean implements FactoryBean<Node> {

    private Logger logger = LoggerFactory.getLogger(ElasticSearchNodeFactoryBean.class);

    private ESConfig esConfig;
    private Node node;

    @Autowired
    public void setEsConfig(ESConfig esConfig) {
        this.esConfig = esConfig;
    }

    @Override
    public Node getObject() throws Exception {
        return getNode();
    }

    @Override
    public Class<?> getObjectType() {
        return Node.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    private Node getNode() {
        String dataPath = esConfig.getDataPath();
        ImmutableSettings.Builder settingsBuilder = ImmutableSettings.settingsBuilder()
                .put("cluster.name", esConfig.getClusterName())
                .put("http.enabled", true);

        if (!dataPath.isEmpty()) {
            settingsBuilder.put("path.data", dataPath);
        }

        Settings settings = settingsBuilder.build();
        node = NodeBuilder.nodeBuilder()
                               .settings(settings)
                               .clusterName(esConfig.getClusterName())
                               .data(true).local(false).node();

        logger.info("Created Embedded ElasticSearch node with cluster name {} and data location at {}",
                esConfig.getClusterName(), esConfig.getDataPath());

        return node;
    }

    @PreDestroy
    private void shutDown() {

        if (node != null) {
            Client client = node.client();
            if (client != null) {
                client.close();
            }
            node.close();
        }
    }
}
