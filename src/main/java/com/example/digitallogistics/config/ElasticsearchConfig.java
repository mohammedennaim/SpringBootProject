package com.example.digitallogistics.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import java.time.Duration;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.example.digitallogistics.repository")
public class ElasticsearchConfig extends ElasticsearchConfiguration {

    @Value("${ELASTICSEARCH_HOST:localhost:9200}")
    private String elasticsearchHost;

    @Override
    public ClientConfiguration clientConfiguration() {
        String[] hosts = elasticsearchHost.split(",");
        return ClientConfiguration.builder()
                .connectedTo(hosts)
                .withConnectTimeout(Duration.ofSeconds(5))
                .withSocketTimeout(Duration.ofSeconds(3))
                .build();
    }
}

