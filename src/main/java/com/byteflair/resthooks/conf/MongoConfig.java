package com.byteflair.resthooks.conf;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Created by Daniel Cerecedo <daniel.cerecedo@byteflair.com> on 15/04/16.
 */
@Configuration
@EnableMongoRepositories(basePackages = "com.byteflair.resthooks")
public class MongoConfig extends AbstractMongoConfiguration{
    @Value("${spring.data.mongodb.db}")
    private String databaseName;
    @Value("${spring.data.mongodb.port}")
    private Integer port;
    @Value("${spring.data.mongodb.host}")
    private String host;
    @Value("${spring.data.mongodb.timeout:10000}")
    private Integer timeout;

    @Override
    protected String getDatabaseName() {
        return databaseName;
    }

    @Override
    @Bean
    public Mongo mongo() throws Exception {
        return new MongoClient(new ServerAddress(host, port),
                               MongoClientOptions.builder().connectTimeout(timeout).build());
    }
}
