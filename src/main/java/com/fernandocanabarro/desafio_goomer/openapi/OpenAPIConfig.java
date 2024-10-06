package com.fernandocanabarro.desafio_goomer.openapi;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
@OpenAPIDefinition
public class OpenAPIConfig {

    @Bean
    public OpenAPI openAPI(){
        return new OpenAPI()
            .info(new Info()
                .title("Desafio Backend Goomer")
                .version("FernandoCanabarroAhnert")
                .description("Este é um projeto baseado no desafio proposto pela Goomer, em que há as funcionalidades de listar, cadastrar, alterar e atualizar restaurantes e seus respectivos produtos.")
                )
                .externalDocs(new ExternalDocumentation()
                    .description("Link GitHub do Desafio proposto")
                    .url("https://github.com/goomerdev/job-dev-backend-interview"));

    }
}
