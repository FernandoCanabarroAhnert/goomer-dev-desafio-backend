package com.fernandocanabarro.desafio_goomer.openapi;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.fernandocanabarro.desafio_goomer.models.product.ProductRequestDTO;
import com.fernandocanabarro.desafio_goomer.models.product.ProductResponseDTO;
import com.fernandocanabarro.desafio_goomer.models.product.embedded.OfferRequestDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

public interface ProductControllerOpenAPI {

    @Operation(
        description = "Consultar todos os Produtos",
        summary = "Endpoint responsável por receber a requisição de Consultar todos os Produtos",
        responses = {
            @ApiResponse(description = "Consulta realizada",responseCode = "200"),
            @ApiResponse(description = "Um usuário não cadastrado faz a requisição",responseCode = "401")
        }
    )
    ResponseEntity<Page<ProductResponseDTO>> findAll(Pageable pageable);

    @Operation(
        description = "Criar um novo Produto",
        summary = "Endpoint responsável por receber a requisição de Criar um novo Produto",
        responses = {
            @ApiResponse(description = "Produto criado",responseCode = "201"),
            @ApiResponse(description = "Um usuário não cadastrado faz a requisição",responseCode = "401"),
            @ApiResponse(description = "Um usuário comum faz a requisição",responseCode = "403"),
            @ApiResponse(description = "Alguma Categoria do Produto ou o Restaurante não foi encontrado",responseCode = "404"),
            @ApiResponse(description = "Algum dado da requisição está inválido",responseCode = "422")
        }
    )
    ResponseEntity<ProductResponseDTO> create(@PathVariable String restaurantId,@RequestBody @Valid ProductRequestDTO dto);

    @Operation(
        description = "Atualizar um novo Produto",
        summary = "Endpoint responsável por receber a requisição de Atualizar um novo Produto",
        responses = {
            @ApiResponse(description = "Produto atualizado",responseCode = "200"),
            @ApiResponse(description = "Um usuário não cadastrado faz a requisição",responseCode = "401"),
            @ApiResponse(description = "Um usuário comum faz a requisição",responseCode = "403"),
            @ApiResponse(description = "Alguma Categoria do Produto ou o Produto não foi encontrado",responseCode = "404"),
            @ApiResponse(description = "Algum dado da requisição está inválido",responseCode = "422")
        }
    )
    ResponseEntity<ProductResponseDTO> update(@PathVariable String id,@RequestBody @Valid ProductRequestDTO dto);

    @Operation(
        description = "Colocar um Produto em oferta",
        summary = "Endpoint responsável por receber a requisição de Colocar um produto em oferta",
        responses = {
            @ApiResponse(description = "Produto foi colocado em oferta",responseCode = "200"),
            @ApiResponse(description = "Um usuário não cadastrado faz a requisição",responseCode = "401"),
            @ApiResponse(description = "Um usuário comum faz a requisição",responseCode = "403"),
            @ApiResponse(description = "Produto não encontrado",responseCode = "404"),
            @ApiResponse(description = "Algum dado da requisição está inválido",responseCode = "422")
        }
    )
    ResponseEntity<ProductResponseDTO> createOffer(@PathVariable String id,@RequestBody @Valid OfferRequestDTO dto);

    @Operation(
        description = "Deletar um Produto",
        summary = "Endpoint responsável por receber a requisição de Deletar um Produto",
        responses = {
            @ApiResponse(description = "Produto deletado",responseCode = "204"),
            @ApiResponse(description = "Um usuário não cadastrado faz a requisição",responseCode = "401"),
            @ApiResponse(description = "Um usuário comum faz a requisição",responseCode = "403"),
            @ApiResponse(description = "Produto não encontrado",responseCode = "404"),
        }
    )
    ResponseEntity<Void> delete(@PathVariable String id);
    
}
