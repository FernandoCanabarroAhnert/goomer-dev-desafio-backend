package com.fernandocanabarro.desafio_goomer.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.ArrayList;

import com.fernandocanabarro.desafio_goomer.models.category.Category;
import com.fernandocanabarro.desafio_goomer.models.category.CategoryDTO;
import com.fernandocanabarro.desafio_goomer.utils.TokenUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class CategoryControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private TokenUtils tokenUtils;

    private String existingId,nonExistingId;
    private String userEmail,userPasword;
    private String adminEmail,adminPassword;
    private String userBearerToken,adminBearerToken;
    private CategoryDTO categoryDTO;
    private List<String> documentsIds = new ArrayList<>();

    @BeforeEach
    public void setup() throws Exception{
        existingId = "67015d61f90ce176dccb9749";
        nonExistingId = "fajhgjhg";
        userEmail = "ana@gmail.com";
        userPasword = "12345Az@";
        adminEmail = "alex@gmail.com";
        adminPassword = "12345Az@";
        userBearerToken = tokenUtils.obtainAccessToken(mockMvc, userEmail, userPasword);
        adminBearerToken = tokenUtils.obtainAccessToken(mockMvc, adminEmail, adminPassword);
        categoryDTO = new CategoryDTO(null, "Nova Categoria");
    }

    @AfterEach
    public void cleanUp() throws Exception{
        for (String id : documentsIds){
            mongoTemplate.remove(new Query(Criteria.where("_id").is(id)),Category.class);
        }
        documentsIds.clear();
    }
    
    @Test
    public void findAllShouldReturnHttpStatus200WhenUserIsLogged() throws Exception{
        mockMvc.perform(get("/categories")
            .header("Authorization","Bearer " + adminBearerToken)
            .accept(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("Doces"));
    }

    @Test
    public void findAllShouldReturnHttpStatus401WhenNoUserIsLogged() throws Exception{
        mockMvc.perform(get("/categories")
            .accept(APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void findProductsByCategoryIdShouldReturnHttpStatus200WhenUserIsLogged() throws Exception{
        mockMvc.perform(get("/categories/{id}/products",existingId)
            .header("Authorization","Bearer " + adminBearerToken)
            .accept(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].name").value("Pudim de Leite Condensado"));
    }

    @Test
    public void findProductsByCategoryIdShouldReturnHttpStatus404WhenCategoryDoesNotExist() throws Exception{
        mockMvc.perform(get("/categories/{id}/products",nonExistingId)
            .header("Authorization","Bearer " + adminBearerToken)
            .accept(APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void findProductsByCategoryIdShouldReturnHttpStatus401WhenNoUserIsLogged() throws Exception{
        mockMvc.perform(get("/categories/{id}/products",existingId)
            .accept(APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void createShouldReturnHttpStatus201WhenAdminIsLoggedAndDataIsValid() throws Exception{
        ResultActions result = mockMvc.perform(post("/categories")
            .header("Authorization","Bearer " + adminBearerToken)
            .content(objectMapper.writeValueAsString(categoryDTO))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Nova Categoria"));
        String responseContent = result.andReturn().getResponse().getContentAsString();
        String id = JsonPath.parse(responseContent).read("id");
        documentsIds.add(id);
    }

    @Test
    public void createShouldReturnHttpStatus401WhenNoUserIsLogged() throws Exception{
        mockMvc.perform(post("/categories")
            .content(objectMapper.writeValueAsString(categoryDTO))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void createShouldReturnHttpStatus403WhenCommonUserIsLogged() throws Exception{
        mockMvc.perform(post("/categories")
            .header("Authorization", "Bearer " + userBearerToken)
            .content(objectMapper.writeValueAsString(categoryDTO))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    public void createShouldReturnHttpStatus422WhenAdminIsLoggedButNameIsBlank() throws Exception{
        categoryDTO.setName("");
        mockMvc.perform(post("/categories")
            .header("Authorization","Bearer " + adminBearerToken)
            .content(objectMapper.writeValueAsString(categoryDTO))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors[0].fieldName").value("name"))
            .andExpect(jsonPath("$.errors[0].message").value("Campo Requerido"));
    }
}
