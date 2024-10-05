package com.fernandocanabarro.desafio_goomer.controllers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;

import com.fernandocanabarro.desafio_goomer.models.category.CategoryDTO;
import com.fernandocanabarro.desafio_goomer.models.product.Product;
import com.fernandocanabarro.desafio_goomer.models.product.ProductRequestDTO;
import com.fernandocanabarro.desafio_goomer.models.product.embedded.OfferRequestDTO;
import com.fernandocanabarro.desafio_goomer.utils.TokenUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ProductControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private TokenUtils tokenUtils;

    private String existingProductId,nonExistingProductId;
    private String existingRestaurantId,nonExistingRestaurantId;
    private String userEmail,userPasword;
    private String adminEmail,adminPassword;
    private String userBearerToken,adminBearerToken;
    private ProductRequestDTO productRequestDTO;
    private OfferRequestDTO offerRequestDTO;

    private List<String> documentsIds = new ArrayList<>();

    @BeforeEach
    public void setup() throws Exception{
        existingProductId = "67015eedf90ce176dccb9755";
        nonExistingProductId = "fajhgjhg";
        existingRestaurantId = "67015ecef90ce176dccb9753";
        nonExistingRestaurantId = "ahjasghjash";
        userEmail = "ana@gmail.com";
        userPasword = "12345Az@";
        adminEmail = "alex@gmail.com";
        adminPassword = "12345Az@";
        userBearerToken = tokenUtils.obtainAccessToken(mockMvc, userEmail, userPasword);
        adminBearerToken = tokenUtils.obtainAccessToken(mockMvc, adminEmail, adminPassword);

        List<CategoryDTO> categories = new ArrayList<>(List.of(new CategoryDTO("67015d61f90ce176dccb9749", null)));
        productRequestDTO = new ProductRequestDTO("Novo Produto", 10.0, "Imagem", categories);

        offerRequestDTO = new OfferRequestDTO("Oferta", 5.0, "05/10/2024 15:00");
    }

    @AfterEach
    public void cleanUp() throws Exception{
        for (String id : documentsIds){
            mongoTemplate.remove(new Query(Criteria.where("_id").is(id)),Product.class);
        }
        documentsIds.clear();
    }

    @Test
    public void findAllShouldReturnHttpStatus200WhenAnyUserIsLogged() throws Exception{
        mockMvc.perform(get("/products")
            .header("Authorization", "Bearer " + adminBearerToken)
            .accept(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id").value("67015eedf90ce176dccb9755"))
            .andExpect(jsonPath("$.content[0].name").value("Picanha no Espeto"))
            .andExpect(jsonPath("$.content[0].price").value(75.9));
    }

    @Test
    public void findAllShouldReturnHttpStatus401WhenNoUserIsLogged() throws Exception{
        mockMvc.perform(get("/products")
            .accept(APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void createShouldReturnHttpStatus201WhenAdminIsLoggedAndDataIsValid() throws Exception{
        ResultActions resultActions = mockMvc.perform(post("/products/{restaurantId}",existingRestaurantId)
            .header("Authorization", "Bearer " + adminBearerToken)
            .content(objectMapper.writeValueAsString(productRequestDTO))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Novo Produto"))
            .andExpect(jsonPath("$.price").value(10.0))
            .andExpect(jsonPath("$.imageUrl").value("Imagem"))
            .andExpect(jsonPath("$.categories[0].name").value("Doces"));
        String response = resultActions.andReturn().getResponse().getContentAsString();
        String id = JsonPath.parse(response).read("id");
        documentsIds.add(id);
    }

    @Test
    public void createShouldReturnHttpStatus401WhenNoUserIsLogged() throws Exception{
        mockMvc.perform(post("/products/{restaurantId}",existingRestaurantId)
            .content(objectMapper.writeValueAsString(productRequestDTO))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void createShouldReturnHttpStatus403WhenCommonUserIsLogged() throws Exception{
        mockMvc.perform(post("/products/{restaurantId}",existingRestaurantId)
            .header("Authorization", "Bearer " + userBearerToken)
            .content(objectMapper.writeValueAsString(productRequestDTO))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    public void createShouldReturnHttpStatus404WhenAdminIsLoggedButRestaurantDoesNotExists() throws Exception{
        mockMvc.perform(post("/products/{restaurantId}",nonExistingRestaurantId)
            .header("Authorization", "Bearer " + adminBearerToken)
            .content(objectMapper.writeValueAsString(productRequestDTO))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void createShouldReturnHttpStatus422WhenAdminIsLoggedButNameIsBlank() throws Exception{
        productRequestDTO.setName("");
        mockMvc.perform(post("/products/{restaurantId}",existingRestaurantId)
            .header("Authorization", "Bearer " + adminBearerToken)
            .content(objectMapper.writeValueAsString(productRequestDTO))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors[0].fieldName").value("name"))
            .andExpect(jsonPath("$.errors[0].message").value("Campo Requerido"));
    }

    @Test
    public void createShouldReturnHttpStatus422WhenAdminIsLoggedButPriceIsNegative() throws Exception{
        productRequestDTO.setPrice(-10.0);
        mockMvc.perform(post("/products/{restaurantId}",existingRestaurantId)
            .header("Authorization", "Bearer " + adminBearerToken)
            .content(objectMapper.writeValueAsString(productRequestDTO))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors[0].fieldName").value("price"))
            .andExpect(jsonPath("$.errors[0].message").value("Valor deve ser positivo"));
    }

    @Test
    public void createShouldReturnHttpStatus422WhenAdminIsLoggedButImageUrlIsBlank() throws Exception{
        productRequestDTO.setImageUrl("");
        mockMvc.perform(post("/products/{restaurantId}",existingRestaurantId)
            .header("Authorization", "Bearer " + adminBearerToken)
            .content(objectMapper.writeValueAsString(productRequestDTO))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors[0].fieldName").value("imageUrl"))
            .andExpect(jsonPath("$.errors[0].message").value("Campo Requerido"));
    }

    @Test
    public void createShouldReturnHttpStatus422WhenAdminIsLoggedButCategoriesIsEmpty() throws Exception{
        productRequestDTO.setCategories(new ArrayList<>());
        mockMvc.perform(post("/products/{restaurantId}",existingRestaurantId)
            .header("Authorization", "Bearer " + adminBearerToken)
            .content(objectMapper.writeValueAsString(productRequestDTO))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors[0].fieldName").value("categories"))
            .andExpect(jsonPath("$.errors[0].message").value("Deve haver pelo menos 1 categoria"));
    }

    @Test
    public void updateShouldReturnHttpStatus201WhenAdminIsLoggedAndDataIsValid() throws Exception{
        ResultActions resultActions = mockMvc.perform(post("/products/{restaurantId}",existingRestaurantId)
            .header("Authorization", "Bearer " + adminBearerToken)
            .content(objectMapper.writeValueAsString(productRequestDTO))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Novo Produto"))
            .andExpect(jsonPath("$.price").value(10.0))
            .andExpect(jsonPath("$.imageUrl").value("Imagem"))
            .andExpect(jsonPath("$.categories[0].name").value("Doces"));
        String response = resultActions.andReturn().getResponse().getContentAsString();
        String id = JsonPath.parse(response).read("id");

        mockMvc.perform(put("/products/{id}",id)
            .header("Authorization", "Bearer " + adminBearerToken)
            .content(objectMapper.writeValueAsString(productRequestDTO))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Novo Produto"))
            .andExpect(jsonPath("$.price").value(10.0))
            .andExpect(jsonPath("$.imageUrl").value("Imagem"))
            .andExpect(jsonPath("$.categories[0].name").value("Doces"));
        documentsIds.add(id);
    }

    @Test
    public void updateShouldReturnHttpStatus401WhenNoUserIsLogged() throws Exception{
        mockMvc.perform(put("/products/{id}",existingProductId)
            .content(objectMapper.writeValueAsString(productRequestDTO))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void updateShouldReturnHttpStatus403WhenCommonUserIsLogged() throws Exception{
        mockMvc.perform(put("/products/{id}",existingProductId)
            .header("Authorization", "Bearer " + userBearerToken)
            .content(objectMapper.writeValueAsString(productRequestDTO))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    public void updateShouldReturnHttpStatus404WhenAdminIsLoggedButProductDoesNotExists() throws Exception{
        mockMvc.perform(put("/products/{id}",nonExistingProductId)
            .header("Authorization", "Bearer " + adminBearerToken)
            .content(objectMapper.writeValueAsString(productRequestDTO))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateShouldReturnHttpStatus422WhenAdminIsLoggedButNameIsBlank() throws Exception{
        productRequestDTO.setName("");
        mockMvc.perform(put("/products/{id}",existingProductId)
            .header("Authorization", "Bearer " + adminBearerToken)
            .content(objectMapper.writeValueAsString(productRequestDTO))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors[0].fieldName").value("name"))
            .andExpect(jsonPath("$.errors[0].message").value("Campo Requerido"));
    }

    @Test
    public void updateShouldReturnHttpStatus422WhenAdminIsLoggedButPriceIsNegative() throws Exception{
        productRequestDTO.setPrice(-10.0);
        mockMvc.perform(put("/products/{id}",existingProductId)
            .header("Authorization", "Bearer " + adminBearerToken)
            .content(objectMapper.writeValueAsString(productRequestDTO))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors[0].fieldName").value("price"))
            .andExpect(jsonPath("$.errors[0].message").value("Valor deve ser positivo"));
    }

    @Test
    public void updateShouldReturnHttpStatus422WhenAdminIsLoggedButImageUrlIsBlank() throws Exception{
        productRequestDTO.setImageUrl("");
        mockMvc.perform(put("/products/{id}",existingProductId)
            .header("Authorization", "Bearer " + adminBearerToken)
            .content(objectMapper.writeValueAsString(productRequestDTO))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors[0].fieldName").value("imageUrl"))
            .andExpect(jsonPath("$.errors[0].message").value("Campo Requerido"));
    }

    @Test
    public void updateShouldReturnHttpStatus422WhenAdminIsLoggedButCategoriesIsEmpty() throws Exception{
        productRequestDTO.setCategories(new ArrayList<>());
        mockMvc.perform(put("/products/{id}",existingProductId)
            .header("Authorization", "Bearer " + adminBearerToken)
            .content(objectMapper.writeValueAsString(productRequestDTO))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors[0].fieldName").value("categories"))
            .andExpect(jsonPath("$.errors[0].message").value("Deve haver pelo menos 1 categoria"));
    }

    @Test
    public void deleteShouldReturnHttpStatus401WhenNoUserIsLogged() throws Exception{
        mockMvc.perform(delete("/products/{id}",nonExistingProductId)
        .content(objectMapper.writeValueAsString(productRequestDTO))
        .contentType(APPLICATION_JSON))
        .andExpect(status().isUnauthorized());
    }

    @Test
    public void deleteShouldReturnHttpStatus403WhenCommonUserIsLogged() throws Exception{
        mockMvc.perform(delete("/products/{id}",nonExistingProductId)
            .header("Authorization", "Bearer " + userBearerToken)
            .content(objectMapper.writeValueAsString(productRequestDTO))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    public void deleteShouldReturnHttpStatus404WhenAdminIsLoggedButProductDoesNotExist() throws Exception{
        mockMvc.perform(delete("/products/{id}",nonExistingProductId)
            .header("Authorization", "Bearer " + adminBearerToken)
            .content(objectMapper.writeValueAsString(productRequestDTO))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void deleteShouldReturnHttpStatus204WhenAdminIsLoggedAndProductExists() throws Exception{
        ResultActions resultActions = mockMvc.perform(post("/products/{restaurantId}",existingRestaurantId)
            .header("Authorization", "Bearer " + adminBearerToken)
            .content(objectMapper.writeValueAsString(productRequestDTO))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Novo Produto"))
            .andExpect(jsonPath("$.price").value(10.0))
            .andExpect(jsonPath("$.imageUrl").value("Imagem"))
            .andExpect(jsonPath("$.categories[0].name").value("Doces"));
        String response = resultActions.andReturn().getResponse().getContentAsString();
        String id = JsonPath.parse(response).read("id");

        mockMvc.perform(delete("/products/{id}",id)
            .header("Authorization", "Bearer " + adminBearerToken)
            .content(objectMapper.writeValueAsString(productRequestDTO))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isNoContent());
    }

    @Test
    public void createOfferShouldReturnHttpStatus200WhenAdminIsLoggedAndDataIsValid() throws Exception{
        ResultActions resultActions = mockMvc.perform(post("/products/{restaurantId}",existingRestaurantId)
            .header("Authorization", "Bearer " + adminBearerToken)
            .content(objectMapper.writeValueAsString(productRequestDTO))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Novo Produto"))
            .andExpect(jsonPath("$.price").value(10.0))
            .andExpect(jsonPath("$.imageUrl").value("Imagem"))
            .andExpect(jsonPath("$.categories[0].name").value("Doces"));
        String response = resultActions.andReturn().getResponse().getContentAsString();
        String id = JsonPath.parse(response).read("id");

        mockMvc.perform(put("/products/offer/{id}",id)
            .content(objectMapper.writeValueAsString(offerRequestDTO))
            .header("Authorization", "Bearer " + adminBearerToken)
            .contentType(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.isInOffer").value(true))
            .andExpect(jsonPath("$.offer.offerPrice").value(5.0));
        documentsIds.add(id);
    }

    @Test
    public void createOfferShouldReturnHttpStatus401WhenNoUserIsLogged() throws Exception{
        mockMvc.perform(put("/products/offer/{id}",nonExistingProductId)
            .content(objectMapper.writeValueAsString(offerRequestDTO))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void createOfferShouldReturnHttpStatus403WhenCommonUserIsLogged() throws Exception{
        mockMvc.perform(put("/products/offer/{id}",nonExistingProductId)
            .header("Authorization", "Bearer " + userBearerToken)
            .content(objectMapper.writeValueAsString(offerRequestDTO))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    public void createOfferShouldReturnHttpStatus404WhenAdminIsLoggedButProductDoesNotExist() throws Exception{
        mockMvc.perform(put("/products/offer/{id}",nonExistingProductId)
            .header("Authorization", "Bearer " + adminBearerToken)
            .content(objectMapper.writeValueAsString(offerRequestDTO))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void createOfferShouldReturnHttpStatus422WhenAdminIsLoggedButDescriptionIsBlank() throws Exception{
        offerRequestDTO.setDescription("");
        mockMvc.perform(put("/products/offer/{id}",existingRestaurantId)
            .header("Authorization", "Bearer " + adminBearerToken)
            .content(objectMapper.writeValueAsString(offerRequestDTO))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors[0].fieldName").value("description"))
            .andExpect(jsonPath("$.errors[0].message").value("Campo Requerido"));
    }

    @Test
    public void createOfferShouldReturnHttpStatus422WhenAdminIsLoggedButPriceIsNegative() throws Exception{
        offerRequestDTO.setOfferPrice(-10.0);
        mockMvc.perform(put("/products/offer/{id}",existingRestaurantId)
            .header("Authorization", "Bearer " + adminBearerToken)
            .content(objectMapper.writeValueAsString(offerRequestDTO))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors[0].fieldName").value("offerPrice"))
            .andExpect(jsonPath("$.errors[0].message").value("Valor deve ser positivo"));
    }

    @Test
    public void createOfferShouldReturnHttpStatus422WhenAdminIsLoggedButOfferEndDateIsBlank() throws Exception{
        offerRequestDTO.setOfferEndDate("");
        mockMvc.perform(put("/products/offer/{id}",existingRestaurantId)
            .header("Authorization", "Bearer " + adminBearerToken)
            .content(objectMapper.writeValueAsString(offerRequestDTO))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors[0].fieldName").value("offerEndDate"))
            .andExpect(jsonPath("$.errors[0].message").value("Campo Requerido"));
    }
}
