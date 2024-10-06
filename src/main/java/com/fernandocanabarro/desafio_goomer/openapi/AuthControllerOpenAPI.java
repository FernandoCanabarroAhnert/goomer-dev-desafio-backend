package com.fernandocanabarro.desafio_goomer.openapi;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import com.fernandocanabarro.desafio_goomer.models.user.RegistrationRequestDTO;
import com.fernandocanabarro.desafio_goomer.models.user.UserDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

public interface AuthControllerOpenAPI {

    @Operation(
        description = "Fazer o Cadastro na API",
        summary = "Endpoint responsável por receber a requisição de cadastro",
        responses = {
            @ApiResponse(description = "Usuário cadastrado",responseCode = "201"),
            @ApiResponse(description = "O e-mail já existe ou algum dado da requisição está inválido",responseCode = "422")
        }
    )
    ResponseEntity<UserDTO> register(@RequestBody @Valid RegistrationRequestDTO dto);
    
}
