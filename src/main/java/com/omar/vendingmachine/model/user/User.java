package com.omar.vendingmachine.model.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

@Document(collection = "user")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    private String id;

    @NotBlank(message = "Username is mandatory")
    private String username;

    @NotBlank(message = "Password is mandatory")
    private String password;

    @Min(value = 0, message = "Deposit can not be less than 0")
    private Integer deposit;

    @DBRef
    private Set<Role> roles = new HashSet<>();

}
