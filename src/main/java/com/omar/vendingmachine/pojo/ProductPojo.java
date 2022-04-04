package com.omar.vendingmachine.pojo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductPojo {

    private String id;

    private String productName;

    private Integer amountAvaillable;

    private Integer cost;

    private String sellerUserName;
}
