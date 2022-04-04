package com.omar.vendingmachine.repository.custom;

import com.omar.vendingmachine.model.user.ERole;
import com.omar.vendingmachine.model.user.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import static com.omar.vendingmachine.constants.UserContants.NAME;

@Component
public class CustomRoleRepositoryImpl implements CustomRoleRepository {
    private final ReactiveMongoTemplate mongoTemplate;

    @Autowired
    public CustomRoleRepositoryImpl(ReactiveMongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Flux<Role> findByName(ERole name) {
        Query query = new Query(Criteria.where(NAME).is(name.toString()));
        return mongoTemplate.find(query, Role.class);
    }
}
