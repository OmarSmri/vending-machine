package com.omar.vendingmachine.repository.custom;

import com.omar.vendingmachine.constants.UserContants;
import com.omar.vendingmachine.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class CustomUserRepositoryImpl implements CustomUserRepository {
    private final ReactiveMongoTemplate mongoTemplate;

    @Autowired
    public CustomUserRepositoryImpl(ReactiveMongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Flux<User> findByUsername(String username) {
        Query query = new Query(Criteria.where(UserContants.USERNAME).is(username));
        return mongoTemplate.find(query, User.class);
    }
}
