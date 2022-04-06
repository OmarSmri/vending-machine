package com.omar.vendingmachine.conf;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@EnableReactiveMongoAuditing
@EnableReactiveMongoRepositories
public class MongoReactiveApplication extends AbstractReactiveMongoConfiguration {

    @Value("${spring.data.mongodb.database}")
    private String dbName;

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create();
    }

    @Override
    protected String getDatabaseName() {
        return dbName;
    }
}
