package com.fernandocanabarro.desafio_goomer.utils;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;

@Component
public class TokenUtils {

    @Value("${security.client-id}")
	private String clientId;

	@Value("${security.client-secret}")
	private String clientSecret;

    public String obtainAccessToken(MockMvc mockMvc,String username,String password) throws Exception{
        MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("grant_type","password");
        params.add("client_id", clientId);
        params.add("username", username);
        params.add("password", password);

        ResultActions resultActions = mockMvc.perform(post("/oauth2/token")
            .params(params)
            .with(httpBasic(clientId, clientSecret))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        String result = resultActions.andReturn().getResponse().getContentAsString();
        JacksonJsonParser jsonParser = new JacksonJsonParser();
        return jsonParser.parseMap(result).get("access_token").toString();
    }
}
