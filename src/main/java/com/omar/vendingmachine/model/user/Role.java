package com.omar.vendingmachine.model.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "role")
@Setter
@Getter
@AllArgsConstructor
public class Role {

    @Id
    private String id;


    private String name;
}
