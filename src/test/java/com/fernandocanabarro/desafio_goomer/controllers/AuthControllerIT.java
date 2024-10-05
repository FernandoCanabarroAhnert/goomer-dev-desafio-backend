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

import com.fernandocanabarro.desafio_goomer.models.restaurant.embedded.AddressRequestDTO;
import com.fernandocanabarro.desafio_goomer.models.user.RegistrationRequestDTO;
import com.fernandocanabarro.desafio_goomer.models.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class AuthControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MongoTemplate mongoTemplate;

    private RegistrationRequestDTO registrationRequestDTO;
    private List<String> documentsIds = new ArrayList<>();

    @BeforeEach
    public void setup() throws Exception{
        AddressRequestDTO addressRequestDTO = new AddressRequestDTO("91349900", "1800", "1800");
        registrationRequestDTO = new RegistrationRequestDTO("Fernando", "fernando@gmail.com", "12345Az@", addressRequestDTO);
    }

    @AfterEach
    public void cleanUp(){
        for (String id : documentsIds){
            mongoTemplate.remove(new Query(Criteria.where("_id").is(id)),User.class);
        }
        documentsIds.clear();
    }

    @Test
    public void registerShouldReturnHttpStatus201WhenDataIsValid() throws Exception{
        ResultActions resultActions = mockMvc.perform(post("/auth/register")
            .content(objectMapper.writeValueAsString(registrationRequestDTO))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.fullName").value("Fernando"))
            .andExpect(jsonPath("$.email").value("fernando@gmail.com"))
            .andExpect(jsonPath("$.roles[0].authority").value("ROLE_USER"));
        String response = resultActions.andReturn().getResponse().getContentAsString();
        String id = JsonPath.parse(response).read("id");
        documentsIds.add(id);
    }

    @Test
    public void registerShouldReturnHttpStatus422WhenFullNameIsBlank() throws Exception{
        registrationRequestDTO.setFullName("");
        mockMvc.perform(post("/auth/register")
            .content(objectMapper.writeValueAsString(registrationRequestDTO))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors[0].fieldName").value("fullName"))
            .andExpect(jsonPath("$.errors[0].message").value("Campo Requerido"));
    }

    @Test
    public void registerShouldReturnHttpStatus422WhenEmailIsInvalid() throws Exception{
        registrationRequestDTO.setEmail("");
        mockMvc.perform(post("/auth/register")
            .content(objectMapper.writeValueAsString(registrationRequestDTO))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors[0].fieldName").value("email"))
            .andExpect(jsonPath("$.errors[0].message").value("Email deve estar em formato válido"));
    }

    @Test
    public void registerShouldReturnHttpStatus422WhenPasswordHasLessThan8Chars() throws Exception{
        registrationRequestDTO.setPassword("Az@1");
        mockMvc.perform(post("/auth/register")
            .content(objectMapper.writeValueAsString(registrationRequestDTO))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors[0].fieldName").value("password"))
            .andExpect(jsonPath("$.errors[0].message").value("Senha deve conter no mínimo 8 caracteres"));
    }

    @Test
    public void registerShouldReturnHttpStatus422WhenPasswordDoesNotHaveAnUpperCaseLetter() throws Exception{
        registrationRequestDTO.setPassword("12345az@");
        mockMvc.perform(post("/auth/register")
            .content(objectMapper.writeValueAsString(registrationRequestDTO))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors[0].fieldName").value("password"))
            .andExpect(jsonPath("$.errors[0].message").value("A Senha deve possuir pelo menos 1 letra maiúscula"));
    }

    @Test
    public void registerShouldReturnHttpStatus422WhenPasswordDoesNotHaveALowerCaseLetter() throws Exception{
        registrationRequestDTO.setPassword("12345AZ@");
        mockMvc.perform(post("/auth/register")
            .content(objectMapper.writeValueAsString(registrationRequestDTO))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors[0].fieldName").value("password"))
            .andExpect(jsonPath("$.errors[0].message").value("A Senha deve possuir pelo menos 1 letra minúscula"));
    }

    @Test
    public void registerShouldReturnHttpStatus422WhenPasswordDoesNotHaveANumber() throws Exception{
        registrationRequestDTO.setPassword("Abcdefg@");
        mockMvc.perform(post("/auth/register")
            .content(objectMapper.writeValueAsString(registrationRequestDTO))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors[0].fieldName").value("password"))
            .andExpect(jsonPath("$.errors[0].message").value("A Senha deve possuir pelo menos 1 número"));
    }

    @Test
    public void registerShouldReturnHttpStatus422WhenPasswordDoesNotHaveASpecialChar() throws Exception{
        registrationRequestDTO.setPassword("12345AzA");
        mockMvc.perform(post("/auth/register")
            .content(objectMapper.writeValueAsString(registrationRequestDTO))
            .contentType(APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors[0].fieldName").value("password"))
            .andExpect(jsonPath("$.errors[0].message").value("A Senha deve possuir pelo menos 1 caractere especial"));
    }

}
