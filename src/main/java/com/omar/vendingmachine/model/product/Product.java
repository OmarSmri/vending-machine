package com.omar.vendingmachine.model.product;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Document(collection = "product")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    private String id;

    @NotBlank(message = "Product Name is mandetory")
    private String productName;

    @NotNull(message = "Amount Availlable is mandetory")
    @Min(value = 0, message = "Amount Availlable can not be less than 0")
    private Integer amountAvaillable;

    @Min(value = 5, message = "Cost cannot be 0 or less")
    private Integer cost;

    @NotBlank(message = "Seller username is mandetory")
    private String sellerUserName;

    private boolean deleted = false;
}
