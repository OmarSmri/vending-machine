package com.omar.vendingmachine.constants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class UserContants {
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String ROLE = "role";
    public static final String BUYER = "buyer";
    public static final String SELLER = "seller";
    public static final String NAME = "name";
    public static final Set<Integer> DEPOSIT_AMOUNTS = new HashSet<>(Arrays.asList(5, 10, 20, 50, 100));
}
