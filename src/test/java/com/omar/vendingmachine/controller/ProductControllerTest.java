package com.omar.vendingmachine.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.omar.vendingmachine.constants.ProductConstants;
import com.omar.vendingmachine.model.product.Product;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {
    private static ObjectMapper mapper = new ObjectMapper();
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
    void testCreateProduct() throws Exception {
        //When sending request to create product with valid values.
        MvcResult result = createBasicProduct(mockMvc, ERole.SELLER);

        //Then, the product is created and have the passed values
        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        List<Product> products = productService.listAll();
        assertThat(products).hasSize(1);
        assertThat(products.get(0).getAmountAvaillable()).isEqualTo(10);
        assertThat(products.get(0).getCost()).isEqualTo(10);
        assertThat(products.get(0).getProductName()).isEqualTo("product");
        assertThat(products.get(0).getSellerUserName()).isEqualTo("user");
    }

    @Test
    void testUpdateProductToInvalidCostValue() throws Exception {
        //Given, a product is created
        createBasicProduct(mockMvc, ERole.SELLER);
        String id = productService.listAll().get(0).getId();

        //When sending a request to update the product and give it cost value that is not valid (not multiple of 5).
        Map<String, String> body = new HashMap<>();
        body.put(ProductConstants.ID, id);
        body.put(ProductConstants.COST, "9");
        String jsonBody = mapper.writeValueAsString(body);
        String auth = "Basic " + Base64.getEncoder().encodeToString("user:password".getBytes());
        MvcResult result = mockMvc.perform(put("/product").header(HttpHeaders.AUTHORIZATION, auth)
                .contentType(MediaType.APPLICATION_JSON).content(jsonBody)).andReturn();

        //Then, the product is not updated and the reponse clarifies the reason and the response is bad request.
        assertThat(result.getResponse().getStatus()).isEqualTo(400);
        assertThat(result.getResponse().getContentAsString()).isEqualTo("The cost should be multiple of 5");
        assertThat(productService.findProductById(id).getCost()).isEqualTo(10);
    }

    @Test
    void testCreateProductFromBuyerAccountForbidden() throws Exception {
        //When trying to create a product using buyer account.
        MvcResult result = createBasicProduct(mockMvc, ERole.BUYER);

        //The product is not created and the response is Forbidden
        assertThat(result.getResponse().getStatus()).isEqualTo(403);
        assertThat(productService.listAll()).hasSize(0);
    }

    @Test
    void testUpdateProduct() throws Exception {
        //Given a product is already crated.
        createBasicProduct(mockMvc, ERole.SELLER);
        String id = productService.listAll().get(0).getId();

        //When, sending a request to update the product with the correct credentials.
        Map<String, String> body = new HashMap<>();
        body.put(ProductConstants.ID, id);
        body.put(ProductConstants.PRODUCT_NAME, "new name");
        String jsonBody = mapper.writeValueAsString(body);
        String auth = "Basic " + Base64.getEncoder().encodeToString("user:password".getBytes());
        mockMvc.perform(put("/product").header(HttpHeaders.AUTHORIZATION, auth)
                .contentType(MediaType.APPLICATION_JSON).content(jsonBody)).andReturn();

        //The product is updated to the passed value.
        assertThat(productService.findProductById(id).getProductName()).isEqualTo("new name");

    }

    @Test
    void testDeleteProduct() throws Exception {
        //Given, a product is already created
        createBasicProduct(mockMvc, ERole.SELLER);
        String id = productService.listAll().get(0).getId();

        //When, sending a request to delete the product by the owner of the product
        String auth = "Basic " + Base64.getEncoder().encodeToString("user:password".getBytes());
        MvcResult result = mockMvc.perform(delete("/product/" + id).header(HttpHeaders.AUTHORIZATION, auth)).andReturn();

        //Then, the product is soft-deleted and the response is ok.
        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        assertThat(productService.findProductById(id).isDeleted()).isTrue();
    }

    @Test
    void testBuyProduct() throws Exception {
        //Given, a product is already created and it is in stock and there is a buyer user with enough deposit.
        createBasicProduct(mockMvc, ERole.SELLER);
        UserTestUtils.createCustomizedUser(mockMvc, "buyerUser", "password", ERole.BUYER);
        User user = customUserDetailService.findByUsername("buyerUser");
        user.setDeposit(100);
        customUserDetailService.saveUser(user);
        String id = productService.listAll().get(0).getId();

        //When sending a request to buy the product using the correct credentials of the buyer account.
        String auth = "Basic " + Base64.getEncoder().encodeToString("buyerUser:password".getBytes());
        MvcResult result = mockMvc.perform(post("/product/buy/" + id).header(HttpHeaders.AUTHORIZATION, auth)).andReturn();

        //Then, the product is purchsed, the change is returned to the buyer, the amountavaillable of the product is reduced.
        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        assertThat(productService.findProductById(id).getAmountAvaillable()).isEqualTo(9);
        assertThat(mapper.readValue(result.getResponse().getContentAsString(), HashMap.class).get("change").toString()).isEqualTo("[50, 20, 20]");
    }

    @Test
    void testBuyOutOfStockProduct() throws Exception {
        //Given a product is created and it is out of stock (amount availlable = 0) and buyer account with enough deposit is created.
        createBasicProduct(mockMvc, ERole.SELLER);
        Product product = productService.listAll().get(0);
        product.setAmountAvaillable(0);
        productService.saveProduct(product);
        UserTestUtils.createCustomizedUser(mockMvc, "buyerUser", "password", ERole.BUYER);
        User user = customUserDetailService.findByUsername("buyerUser");
        user.setDeposit(100);
        customUserDetailService.saveUser(user);

        //When, a request to buy the product is sent.
        String id = productService.listAll().get(0).getId();
        String auth = "Basic " + Base64.getEncoder().encodeToString("buyerUser:password".getBytes());
        MvcResult result = mockMvc.perform(post("/product/buy/" + id).header(HttpHeaders.AUTHORIZATION, auth)).andReturn();

        //Then, the purchase operation is not completed and the response clarifies that the product is out of stock.
        assertThat(result.getResponse().getContentAsString()).contains("is out of stock");
        assertThat(productService.findProductById(id).getAmountAvaillable()).isEqualTo(0);
    }

    @Test
    void testBuyProductWithoutDeposit() throws Exception {
        //Given, a product is created and in stock. A buyer account is created with 0 deposit.
        createBasicProduct(mockMvc, ERole.SELLER);
        String id = productService.listAll().get(0).getId();
        UserTestUtils.createCustomizedUser(mockMvc, "buyerUser", "password", ERole.BUYER);

        //When, sending a request to buy the product.
        String auth = "Basic " + Base64.getEncoder().encodeToString("buyerUser:password".getBytes());
        MvcResult result = mockMvc.perform(post("/product/buy/" + id).header(HttpHeaders.AUTHORIZATION, auth)).andReturn();

        //Then, the purchase is not complete and the product stock is not reduced. The response clarifies the reason.
        assertThat(result.getResponse().getContentAsString()).isEqualTo("User does not have suffecient funds");
        assertThat(productService.findProductById(id).getAmountAvaillable()).isEqualTo(10);
    }

    @Test
    void testBuyDeletedProduct() throws Exception {
        //Given a product is created and it is soft-deleted. A buyer account with enough deposit is created.
        createBasicProduct(mockMvc, ERole.SELLER);
        Product product = productService.listAll().get(0);
        product.setDeleted(true);
        productService.saveProduct(product);
        UserTestUtils.createCustomizedUser(mockMvc, "buyerUser", "password", ERole.BUYER);
        User user = customUserDetailService.findByUsername("buyerUser");
        user.setDeposit(100);
        customUserDetailService.saveUser(user);

        //When, a request to buy the product is sent.
        String id = productService.listAll().get(0).getId();
        String auth = "Basic " + Base64.getEncoder().encodeToString("buyerUser:password".getBytes());
        MvcResult result = mockMvc.perform(post("/product/buy/" + id).header(HttpHeaders.AUTHORIZATION, auth)).andReturn();

        //Then, the purchase operation is not completed and the response clarifies that the product is not availlable. The stock is not reduced.
        assertThat(result.getResponse().getContentAsString()).contains("No Product " + id + " availlable to Purchase");
        assertThat(productService.findProductById(id).getAmountAvaillable()).isEqualTo(10);
    }

    /**
     * Creates basic product using account wit the passed role. The created product has:
     *  product name: product
     *  cost: 10
     *  amount availlable: 10
     *  seller id: user
     * @param mockMvc
     * @param role
     * @return
     * @throws Exception
     */
    private static MvcResult createBasicProduct(MockMvc mockMvc, ERole role) throws Exception {
        UserTestUtils.createBaiscUser(mockMvc, role);
        Map<String, String> body = new HashMap<>();
        body.put(ProductConstants.PRODUCT_NAME, "product");
        body.put(ProductConstants.COST, "10");
        body.put(ProductConstants.AMOUNT_AVAILLABLE, "10");
        String jsonBody = mapper.writeValueAsString(body);
        String auth = "Basic " + Base64.getEncoder().encodeToString("user:password".getBytes());
        return mockMvc.perform(post("/product").header(HttpHeaders.AUTHORIZATION, auth)
                .contentType(MediaType.APPLICATION_JSON).content(jsonBody)).andReturn();
    }

}
