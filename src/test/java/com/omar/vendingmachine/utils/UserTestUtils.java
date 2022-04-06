package com.omar.vendingmachine.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.omar.vendingmachine.constants.UserContants;
import com.omar.vendingmachine.model.user.ERole;
import com.omar.vendingmachine.service.RoleService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class UserTestUtils {
    private static ObjectMapper mapper = new ObjectMapper();

    /**
     * Creates basic user for the testing purposes. The created user have the role passed as input and holds:
     *  username: user
     *  password: password
     * @param mockMvc
     * @param role
     * @return
     * @throws Exception
     */
    public static MvcResult createBaiscUser(MockMvc mockMvc, ERole role) throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put(UserContants.USERNAME, "user");
        body.put(UserContants.PASSWORD, "password");
        body.put(UserContants.ROLE, role.name());
        String jsonBody = mapper.writeValueAsString(body);
        return mockMvc.perform(post("/user/signup").contentType(MediaType.APPLICATION_JSON).content(jsonBody)).andReturn();
    }

    /**
     * Creates customized user for testing purposes where the created user holds the values passed as input.
     * @param mockMvc
     * @param username
     * @param password
     * @param role
     * @return
     * @throws Exception
     */
    public static MvcResult createCustomizedUser(MockMvc mockMvc, String username, String password, ERole role) throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put(UserContants.USERNAME, username);
        body.put(UserContants.PASSWORD, password);
        body.put(UserContants.ROLE, role.name());
        String jsonBody = mapper.writeValueAsString(body);
        return mockMvc.perform(post("/user/signup").contentType(MediaType.APPLICATION_JSON).content(jsonBody)).andReturn();
    }

    /**
     * Creates the common roles for testing purposes.
     * @param roleService
     */
    public static void createRoles(RoleService roleService) {
        roleService.createRole(ERole.SELLER);
        roleService.createRole(ERole.BUYER);
    }
}
