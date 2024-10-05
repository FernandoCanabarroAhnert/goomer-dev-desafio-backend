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

import com.fernandocanabarro.desafio_goomer.models.restaurant.Restaurant;
import com.fernandocanabarro.desafio_goomer.models.restaurant.RestaurantRequestDTO;
import com.fernandocanabarro.desafio_goomer.models.restaurant.embedded.AddressRequestDTO;
import com.fernandocanabarro.desafio_goomer.models.restaurant.embedded.CoordinatesDTO;
import com.fernandocanabarro.desafio_goomer.utils.TokenUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class RestaurantControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private TokenUtils tokenUtils;

    private String existingRestaurantId,nonExistingRestaurantId;
    private String userEmail,userPasword;
    private String adminEmail,adminPassword;
    private String userBearerToken,adminBearerToken;
    private String longitude,latitude;
    private RestaurantRequestDTO restaurantRequestDTO;

    private List<String> documentsIds = new ArrayList<>();

    @BeforeEach
    public void setup() throws Exception{
        existingRestaurantId = "67015ecef90ce176dccb9753";
        nonExistingRestaurantId = "ahjasghjash";
        userEmail = "ana@gmail.com";
        userPasword = "12345Az@";
        adminEmail = "alex@gmail.com";
        adminPassword = "12345Az@";
        userBearerToken = tokenUtils.obtainAccessToken(mockMvc, userEmail, userPasword);
        adminBearerToken = tokenUtils.obtainAccessToken(mockMvc, adminEmail, adminPassword);
        longitude = "-51.225755";
        latitude = "-30.028448";

        AddressRequestDTO addressRequestDTO = new AddressRequestDTO("91349900", "1800", "1800");
        Map<String,String> openingHours = new HashMap<>();
        openingHours.put("Segunda a Sexta","11h-23h");
        openingHours.put("Sábado e Domingo","11h-21h");
        List<String> tags = new ArrayList<>(Arrays.asList("tag"));
        restaurantRequestDTO = new RestaurantRequestDTO("Novo Restaurante", "Imagem",
             addressRequestDTO, new CoordinatesDTO(1.0,1.0),openingHours,tags);
    }

    @AfterEach
    public void cleanUp() throws Exception{
        for (String id : documentsIds){
            mongoTemplate.remove(new Query(Criteria.where("_id").is(id)),Restaurant.class);
        }
        documentsIds.clear();
    }

    @Test
    public void findAllShouldReturnHttpStatus200WhenAnyUserIsLogged() throws Exception{
        mockMvc.perform(get("/restaurants")
            .header("Authorization", "Bearer " + userBearerToken)
            .accept(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id").value("66ff3959eea42d82500a5459"))
            .andExpect(jsonPath("$.content[0].name").value("Churrascaria Galpão Crioulo"))
            .andExpect(jsonPath("$.content[0].imageUrl").value("https://exemplo.com/galpao-crioulo.jpg"));
    }

    @Test
    public void findAllShouldReturnHttpStatus401WhenNoUserIsLogged() throws Exception{
        mockMvc.perform(get("/restaurants")
            .accept(APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void findByCoordinatesShouldReturnHttpStatus200WhenAnyUserIsLogged() throws Exception{
        mockMvc.perform(get("/restaurants/geo?longitude={longitude}&latitude={latitude}",longitude,latitude)
            .header("Authorization", "Bearer " + userBearerToken)
            .accept(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value("66ff3959eea42d82500a5459"))
            .andExpect(jsonPath("$[0].name").value("Churrascaria Galpão Crioulo"))
            .andExpect(jsonPath("$[0].imageUrl").value("https://exemplo.com/galpao-crioulo.jpg"));
    }

    @Test
    public void findByCoordinatesShouldReturnHttpStatus401WhenNoUserIsLogged() throws Exception{
        mockMvc.perform(get("/restaurants/geo?longitude=%s&latitude=%s",longitude,latitude)
            .accept(APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void findByCoordinatesNearShouldReturnHttpStatus200WhenAnyUserIsLogged() throws Exception{
        mockMvc.perform(get("/restaurants/geo/nearMe")
            .header("Authorization", "Bearer " + userBearerToken)
            .accept(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value("66ff39eceea42d82500a5461"))
            .andExpect(jsonPath("$[0].name").value("Hamburgueria 1903"))
            .andExpect(jsonPath("$[0].imageUrl").value("https://exemplo.com/hamburgueria-1903.jpg"));
    }

    @Test
    public void findByCoordinatesNearShouldReturnHttpStatus401WhenNoUserIsLogged() throws Exception{
        mockMvc.perform(get("/restaurants/geo/nearMe")
            .accept(APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void findProductsByRestaurantIdShouldReturnHttpStatus200WhenRestaurantExists() throws Exception{
        mockMvc.perform(get("/restaurants/{id}/products",existingRestaurantId)
            .header("Authorization", "Bearer " + userBearerToken)
            .accept(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id").value("66ff4052c84da8080960ba1f"))
            .andExpect(jsonPath("$.content[0].name").value("Picanha no Espeto"))
            .andExpect(jsonPath("$.content[0].price").value(75.9));
    }

    @Test
    public void findProductsByRestaurantIdShouldReturnHttpStatus401WhenNoUserIsLogged() throws Exception{
        mockMvc.perform(get("/restaurants/{id}/products",existingRestaurantId)
            .accept(APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void findProductsByRestaurantIdShouldReturnHttpStatus404WhenRestaurantDoesNotExist() throws Exception{
        mockMvc.perform(get("/restaurants/{id}/products",nonExistingRestaurantId)
            .header("Authorization", "Bearer " + userBearerToken)
            .accept(APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void findByShouldReturnHttpStatus200WhenRestaurantExists() throws Exception{
        mockMvc.perform(get("/restaurants/{id}",existingRestaurantId)
            .header("Authorization", "Bearer " + userBearerToken)
            .accept(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("66ff3959eea42d82500a5459"))
            .andExpect(jsonPath("$.name").value("Churrascaria Galpão Crioulo"))
            .andExpect(jsonPath("$.imageUrl").value("https://exemplo.com/galpao-crioulo.jpg"));
    }

    @Test
    public void findByShouldReturnHttpStatus401WhenNoUserIsLogged() throws Exception{
        mockMvc.perform(get("/restaurants/{id}",existingRestaurantId)
            .accept(APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void findByShouldReturnHttpStatus404WhenRestaurantDoesNotExist() throws Exception{
        mockMvc.perform(get("/restaurants/{id}",nonExistingRestaurantId)
            .header("Authorization", "Bearer " + userBearerToken)
            .accept(APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void createShouldReturnHttpStatus201WhenAdminIsLoggedAndDataIsValid() throws Exception{
        ResultActions resultActions = mockMvc.perform(post("/restaurants")
            .header("Authorization","Bearer " + adminBearerToken)
            .content(objectMapper.writeValueAsString(restaurantRequestDTO))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Novo Restaurante"))
            .andExpect(jsonPath("$.imageUrl").value("Imagem"))
            .andExpect(jsonPath("$.address.cep").value("91349-900"));
        String response = resultActions.andReturn().getResponse().getContentAsString();
        String id = JsonPath.parse(response).read("id");
        documentsIds.add(id);
    }

    @Test
    public void createShouldReturnHttpStatus401WhenNoUserIsLogged() throws Exception{
        mockMvc.perform(post("/restaurants")
            .content(objectMapper.writeValueAsString(restaurantRequestDTO))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void createShouldReturnHttpStatus403WhenCommonUserIsLogged() throws Exception{
        mockMvc.perform(post("/restaurants")
            .header("Authorization", "Bearer " + userBearerToken)
            .content(objectMapper.writeValueAsString(restaurantRequestDTO))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    public void createShouldReturnHttpStatus422WhenAdminIsLoggedButNameIsBlank() throws Exception {
        restaurantRequestDTO.setName("");
        mockMvc.perform(post("/restaurants")
            .header("Authorization","Bearer " + adminBearerToken)
            .content(objectMapper.writeValueAsString(restaurantRequestDTO))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors[0].fieldName").value("name"))
            .andExpect(jsonPath("$.errors[0].message").value("Campo Requerido"));
    }

    @Test
    public void createShouldReturnHttpStatus422WhenAdminIsLoggedButImageUrlIsBlank() throws Exception {
        restaurantRequestDTO.setImageUrl("");
        mockMvc.perform(post("/restaurants")
            .header("Authorization","Bearer " + adminBearerToken)
            .content(objectMapper.writeValueAsString(restaurantRequestDTO))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors[0].fieldName").value("imageUrl"))
            .andExpect(jsonPath("$.errors[0].message").value("Campo Requerido"));
    }

    @Test
    public void createShouldReturnHttpStatus422WhenAdminIsLoggedButCepIsBlank() throws Exception {
        restaurantRequestDTO.getOpeningHours().clear();
        mockMvc.perform(post("/restaurants")
            .header("Authorization","Bearer " + adminBearerToken)
            .content(objectMapper.writeValueAsString(restaurantRequestDTO))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors[0].fieldName").value("openingHours"))
            .andExpect(jsonPath("$.errors[0].message").value("Deve haver os dias e horários de funcionamento"));
    }

    @Test
    public void createShouldReturnHttpStatus422WhenAdminIsLoggedButTagsIsEmpty() throws Exception {
        restaurantRequestDTO.getTags().clear();;
        mockMvc.perform(post("/restaurants")
            .header("Authorization","Bearer " + adminBearerToken)
            .content(objectMapper.writeValueAsString(restaurantRequestDTO))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors[0].fieldName").value("tags"))
            .andExpect(jsonPath("$.errors[0].message").value("Deve haver pelo menos 1 tag"));
    }

    @Test
    public void updateShouldReturnHttpStatus201WhenAdminIsLoggedAndDataIsValid() throws Exception{
        ResultActions resultActions = mockMvc.perform(post("/restaurants")
            .header("Authorization","Bearer " + adminBearerToken)
            .content(objectMapper.writeValueAsString(restaurantRequestDTO))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Novo Restaurante"))
            .andExpect(jsonPath("$.imageUrl").value("Imagem"))
            .andExpect(jsonPath("$.address.cep").value("91349-900"));
        String response = resultActions.andReturn().getResponse().getContentAsString();
        String id = JsonPath.parse(response).read("id");
        mockMvc.perform(put("/restaurants/{id}",id)
            .header("Authorization","Bearer " + adminBearerToken)
            .content(objectMapper.writeValueAsString(restaurantRequestDTO))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Novo Restaurante"))
            .andExpect(jsonPath("$.imageUrl").value("Imagem"))
            .andExpect(jsonPath("$.address.cep").value("91349-900"));
        documentsIds.add(id);
    }

    @Test
    public void updateShouldReturnHttpStatus401WhenNoUserIsLogged() throws Exception{
        mockMvc.perform(put("/restaurants/{id}",nonExistingRestaurantId)
            .content(objectMapper.writeValueAsString(restaurantRequestDTO))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void updateShouldReturnHttpStatus403WhenCommonUserIsLogged() throws Exception{
        mockMvc.perform(put("/restaurants/{id}",nonExistingRestaurantId)
            .header("Authorization", "Bearer " + userBearerToken)
            .content(objectMapper.writeValueAsString(restaurantRequestDTO))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    public void updateShouldReturnHttpStatus404WhenRestaurantDoesNotExist() throws Exception{
        mockMvc.perform(put("/restaurants/{id}",nonExistingRestaurantId)
            .header("Authorization", "Bearer " + adminBearerToken)
            .content(objectMapper.writeValueAsString(restaurantRequestDTO))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateShouldReturnHttpStatus422WhenAdminIsLoggedButNameIsBlank() throws Exception {
        restaurantRequestDTO.setName("");
        mockMvc.perform(put("/restaurants/{id}",nonExistingRestaurantId)
            .header("Authorization","Bearer " + adminBearerToken)
            .content(objectMapper.writeValueAsString(restaurantRequestDTO))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors[0].fieldName").value("name"))
            .andExpect(jsonPath("$.errors[0].message").value("Campo Requerido"));
    }

    @Test
    public void updateShouldReturnHttpStatus422WhenAdminIsLoggedButImageUrlIsBlank() throws Exception {
        restaurantRequestDTO.setImageUrl("");
        mockMvc.perform(put("/restaurants/{id}",nonExistingRestaurantId)
            .header("Authorization","Bearer " + adminBearerToken)
            .content(objectMapper.writeValueAsString(restaurantRequestDTO))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors[0].fieldName").value("imageUrl"))
            .andExpect(jsonPath("$.errors[0].message").value("Campo Requerido"));
    }

    @Test
    public void updateShouldReturnHttpStatus422WhenAdminIsLoggedButCepIsBlank() throws Exception {
        restaurantRequestDTO.getOpeningHours().clear();
        mockMvc.perform(put("/restaurants/{id}",nonExistingRestaurantId)
            .header("Authorization","Bearer " + adminBearerToken)
            .content(objectMapper.writeValueAsString(restaurantRequestDTO))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors[0].fieldName").value("openingHours"))
            .andExpect(jsonPath("$.errors[0].message").value("Deve haver os dias e horários de funcionamento"));
    }

    @Test
    public void updateShouldReturnHttpStatus422WhenAdminIsLoggedButTagsIsEmpty() throws Exception {
        restaurantRequestDTO.getTags().clear();;
        mockMvc.perform(put("/restaurants/{id}",nonExistingRestaurantId)
            .header("Authorization","Bearer " + adminBearerToken)
            .content(objectMapper.writeValueAsString(restaurantRequestDTO))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors[0].fieldName").value("tags"))
            .andExpect(jsonPath("$.errors[0].message").value("Deve haver pelo menos 1 tag"));
    }

    @Test
    public void deleteShouldReturnHttpStatus204WhenAdminIsLoggedAndRestaurantExists() throws Exception{
        ResultActions resultActions = mockMvc.perform(post("/restaurants")
            .header("Authorization","Bearer " + adminBearerToken)
            .content(objectMapper.writeValueAsString(restaurantRequestDTO))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Novo Restaurante"))
            .andExpect(jsonPath("$.imageUrl").value("Imagem"))
            .andExpect(jsonPath("$.address.cep").value("91349-900"));
        String response = resultActions.andReturn().getResponse().getContentAsString();
        String id = JsonPath.parse(response).read("id");
        mockMvc.perform(delete("/restaurants/{id}",id)
            .header("Authorization", "Bearer " + adminBearerToken)
            .content(objectMapper.writeValueAsString(restaurantRequestDTO))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isNoContent());
    }

    @Test
    public void deleteShouldReturnHttpStatus401WhenNoUserIsLogged() throws Exception{
        mockMvc.perform(delete("/restaurants/{id}",nonExistingRestaurantId)
            .content(objectMapper.writeValueAsString(restaurantRequestDTO))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void deleteShouldReturnHttpStatus403WhenCommonUserIsLogged() throws Exception{
        mockMvc.perform(delete("/restaurants/{id}",nonExistingRestaurantId)
            .header("Authorization", "Bearer " + userBearerToken)
            .content(objectMapper.writeValueAsString(restaurantRequestDTO))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    public void deleteShouldReturnHttpStatus404WhenRestaurantDoesNotExist() throws Exception{
        mockMvc.perform(delete("/restaurants/{id}",nonExistingRestaurantId)
            .header("Authorization", "Bearer " + adminBearerToken)
            .content(objectMapper.writeValueAsString(restaurantRequestDTO))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }
}
