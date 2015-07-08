package org.oruko.dictionary.elasticsearch;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Factor for creating @{link Client} bean
 *
 * Created by Dadepo Aderemi.
 */
@Component
public class ElasticSearchClientFactoryBean implements FactoryBean<Client> {

    private ESConfig esConfig;

    @Autowired
    public void setEsConfig(ESConfig esConfig) {
        this.esConfig = esConfig;
    }

    @Override
    public Client getObject() throws Exception {
        return getClient();
    }

    @Override
    public Class<?> getObjectType() {
        return TransportClient.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    private Client getClient() {
        Settings settings = ImmutableSettings.settingsBuilder()
                                             .put("cluster.name", esConfig.getClusterName())
                                             .build();
        return new TransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(esConfig.getHostName(), esConfig.getPort()));
    }
}
