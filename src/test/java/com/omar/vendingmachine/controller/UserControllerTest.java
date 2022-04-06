package com.omar.vendingmachine.controller;

import com.omar.vendingmachine.model.user.ERole;
import com.omar.vendingmachine.model.user.User;
import com.omar.vendingmachine.service.CustomUserDetailService;
import com.omar.vendingmachine.service.ProductService;
import com.omar.vendingmachine.service.RoleService;
import com.omar.vendingmachine.utils.UserTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Base64;
import java.util.List;

import static com.omar.vendingmachine.utils.UserTestUtils.createBaiscUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ProductService productService;
    @Autowired
    private CustomUserDetailService customUserDetailService;
    @Autowired
    private RoleService roleService;

    @BeforeEach
    void initEach() {
        productService.deleteAll();
        customUserDetailService.deleteAll();
        UserTestUtils.createRoles(roleService);
    }

    @Test
    void testDepositDefinedAmount() throws Exception {
        //Given, a buyer user is already created.
        createBaiscUser(mockMvc, ERole.BUYER);

        //When, sending a request to deposit a defined (valid) amount to the account.
        String auth = "Basic " + Base64.getEncoder().encodeToString("user:password".getBytes());
        MvcResult result = mockMvc.perform(put("/user/deposit/5")
                .header(HttpHeaders.AUTHORIZATION, auth)).andReturn();

        //Then, the amount is deposited and the deposit amount is increased.
        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        List<User> users = customUserDetailService.listAll();
        assertThat(users.get(0).getDeposit()).isEqualTo(5);
    }

    @Test
    void testDepositDefinedAmountBySeller() throws Exception {
        //Given, a seller user is already created.
        createBaiscUser(mockMvc, ERole.SELLER);

        //When, sending a request to deposit a defined (valid) amount to the seller account.
        String auth = "Basic " + Base64.getEncoder().encodeToString("user:password".getBytes());
        MvcResult result = mockMvc.perform(put("/user/deposit/5")
                .header(HttpHeaders.AUTHORIZATION, auth)).andReturn();

        //Then, the amount is not deposited and the response is Forbidden
        assertThat(result.getResponse().getStatus()).isEqualTo(403);
        List<User> users = customUserDetailService.listAll();
    }

    @Test
    void testDepositNotDefindedAmount() throws Exception {
        //Given, a buyer account is already created.
        createBaiscUser(mockMvc, ERole.BUYER);

        //When, sending a request to deposit amount that is not defined (not among the valid amounts).
        String auth = "Basic " + Base64.getEncoder().encodeToString("user:password".getBytes());
        MvcResult result = mockMvc.perform(put("/user/deposit/7")
                .header(HttpHeaders.AUTHORIZATION, auth)).andReturn();

        //Then, the deposit is not completed (the deposit value is not changed) and the response is Bad Request.
        assertThat(result.getResponse().getStatus()).isEqualTo(400);
        List<User> users = customUserDetailService.listAll();
        assertThat(users.get(0).getDeposit()).isEqualTo(0);
    }

    @Test
    void testResetDepoist() throws Exception {
        //Given, a buyer account is already created with 100 deposit.
        createBaiscUser(mockMvc, ERole.BUYER);
        User user = customUserDetailService.listAll().get(0);
        user.setDeposit(100);
        customUserDetailService.saveUser(user);

        //When, sending a request to resst the deposit.
        String auth = "Basic " + Base64.getEncoder().encodeToString("user:password".getBytes());
        MvcResult result = mockMvc.perform(put("/user/deposit/reset")
                .header(HttpHeaders.AUTHORIZATION, auth)).andReturn();

        //The deposit is reset to 0 and the response is Ok.
        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        List<User> users = customUserDetailService.listAll();
        assertThat(users.get(0).getDeposit()).isEqualTo(0);

    }
}
