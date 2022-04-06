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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {
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
    void testRegisterBuyer() throws Exception {
        //When sending request to create user as buyer.
        MvcResult result = createBaiscUser(mockMvc, ERole.BUYER);

        //Then, the response is 200 and the user is created with buyer role.
        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        List<User> users = customUserDetailService.listAll();
        assertThat(users).hasSize(1);
        assertThat(users.get(0).getRoles().iterator().next().getName()).isEqualTo(ERole.BUYER.name());
    }

    @Test
    void testRegisterSeller() throws Exception {
        //when sending request to create user as seller.
        MvcResult result = createBaiscUser(mockMvc, ERole.SELLER);

        //Then, the response is 200 and the user is created with seller role.
        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        List<User> users = customUserDetailService.listAll();
        assertThat(users).hasSize(1);
        assertThat(users.get(0).getRoles().iterator().next().getName()).isEqualTo(ERole.SELLER.name());
    }

    @Test
    void testRegisterUserWithExistingUsername() throws Exception {
        //given is a registerd user with username: user
        createBaiscUser(mockMvc, ERole.SELLER);

        //When sending request to create a user with username: user
        MvcResult result = createBaiscUser(mockMvc, ERole.SELLER);

        //Then, the user is not created and the reponse says that there is already registerd user with that username
        assertThat(result.getResponse().getContentAsString()).isEqualTo("User with user username already exists");
    }

    @Test
    void testLogin() throws Exception {
        //given is registered user with credentials: username: user, password: password
        createBaiscUser(mockMvc, ERole.BUYER);

        //When sending a request to log the user in with the correct credentials
        String auth = "Basic " + Base64.getEncoder().encodeToString("user:password".getBytes());
        MvcResult result = mockMvc.perform(post("/user/login")
                .header(HttpHeaders.AUTHORIZATION, auth)).andReturn();

        //Then the user is logged in and the response is ok.
        assertThat(result.getResponse().getStatus()).isEqualTo(200);

    }

    @Test
    void testLoginWithInvalidCredentials() throws Exception {
        //given is registered user with credentials: username: user, password: password
        createBaiscUser(mockMvc, ERole.BUYER);

        //When sending a request to log the user in with the correct credentials
        String auth = "Basic " + Base64.getEncoder().encodeToString("user:pass".getBytes());
        MvcResult result = mockMvc.perform(post("/user/login")
                .header(HttpHeaders.AUTHORIZATION, auth)).andReturn();

        //Then the user is not loggen in and the reponse is unauthorized.
        assertThat(result.getResponse().getStatus()).isEqualTo(401);
    }

}
