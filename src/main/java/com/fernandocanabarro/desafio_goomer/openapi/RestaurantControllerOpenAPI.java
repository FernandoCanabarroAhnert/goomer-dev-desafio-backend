package com.fernandocanabarro.desafio_goomer.openapi;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.fernandocanabarro.desafio_goomer.models.product.ProductResponseDTO;
import com.fernandocanabarro.desafio_goomer.models.restaurant.RestaurantRequestDTO;
import com.fernandocanabarro.desafio_goomer.models.restaurant.RestaurantResponseDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

import java.util.List;

public interface RestaurantControllerOpenAPI {

    @Operation(
        description = "Consultar todos os Restaurantes",
        summary = "Endpoint responsável por receber a requisição de Consultar todos os Restaurantes",
        responses = {
            @ApiResponse(description = "Consulta realizada",responseCode = "200"),
            @ApiResponse(description = "Um usuário não cadastrado faz a requisição",responseCode = "401")
        }
    )
    ResponseEntity<Page<RestaurantResponseDTO>> findAll(Pageable pageable, @RequestParam(name = "tag",defaultValue = "") String tag);

    @Operation(
        description = "Consultar Restaurantes dado uma coordenada",
        summary = "Endpoint responsável por receber a requisição de Consultar Restaurantes dado uma coordenada",
        responses = {
            @ApiResponse(description = "Consulta realizada",responseCode = "200"),
            @ApiResponse(description = "Um usuário não cadastrado faz a requisição",responseCode = "401")
        }
    )
    ResponseEntity<List<RestaurantResponseDTO>> findByCoordinates(
        @RequestParam(name = "tag",defaultValue = "") String tag,
        @RequestParam(name = "longitude") String longitude,
        @RequestParam(name = "latitude") String latitude,
        @RequestParam(name = "maxDistance",defaultValue = "") String maxDistance
    );

    @Operation(
        description = "Consultar Restaurantes dado as coordenadas do endereço do usuário cadastrado",
        summary = "Endpoint responsável por receber a requisição de Consultar Restaurantes dado as coordenadas do endereço do usuário cadastrado",
        responses = {
            @ApiResponse(description = "Consulta realizada",responseCode = "200"),
            @ApiResponse(description = "Um usuário não cadastrado faz a requisição",responseCode = "401")
        }
    )
    ResponseEntity<List<RestaurantResponseDTO>> findByCoordinatesNearMe(
        @RequestParam(name = "tag",defaultValue = "") String tag,
        @RequestParam(name = "maxDistance",defaultValue = "") String maxDistance
    );

    @Operation(
        description = "Consultar todos os Produtos de um Restaurante",
        summary = "Endpoint responsável por receber a requisição de Consultar todos os Produtos de um Restaurante",
        responses = {
            @ApiResponse(description = "Consulta realizada",responseCode = "200"),
            @ApiResponse(description = "Um usuário não cadastrado faz a requisição",responseCode = "401"),
            @ApiResponse(description = "Restaurante não encontrado",responseCode = "404")
        }
    )
    ResponseEntity<Page<ProductResponseDTO>> findProductsByRestaurantId(Pageable pageable, @PathVariable String id);

    @Operation(
        description = "Consultar um Restaurante por Id",
        summary = "Endpoint responsável por receber a requisição de Consultar um Restaurante por Id",
        responses = {
            @ApiResponse(description = "Consulta realizada",responseCode = "200"),
            @ApiResponse(description = "Um usuário não cadastrado faz a requisição",responseCode = "401"),
            @ApiResponse(description = "Restaurante não encontrado",responseCode = "404")
        }
    )
    ResponseEntity<RestaurantResponseDTO> findById(@PathVariable String id);

    @Operation(
        description = "Criar um novo Restaurante",
        summary = "Endpoint responsável por receber a requisição de Criar um novo Restaurante",
        responses = {
            @ApiResponse(description = "Restaurante criado",responseCode = "201"),
            @ApiResponse(description = "Um usuário não cadastrado faz a requisição",responseCode = "401"),
            @ApiResponse(description = "Um usuário comum faz a requisição",responseCode = "403"),
            @ApiResponse(description = "Algum dado da requisição está inválido",responseCode = "422")
        }
    )
    ResponseEntity<RestaurantResponseDTO> create(@RequestBody @Valid RestaurantRequestDTO dto);

    @Operation(
        description = "Atualizar um Restaurante",
        summary = "Endpoint responsável por receber a requisição de Atualizar um Restaurante",
        responses = {
            @ApiResponse(description = "Restaurante atualizado",responseCode = "200"),
            @ApiResponse(description = "Um usuário não cadastrado faz a requisição",responseCode = "401"),
            @ApiResponse(description = "Um usuário comum faz a requisição",responseCode = "403"),
            @ApiResponse(description = "Restaurante não foi encontrado",responseCode = "404"),
            @ApiResponse(description = "Algum dado da requisição está inválido",responseCode = "422")
        }
    )
    ResponseEntity<RestaurantResponseDTO> update(@PathVariable String id,@RequestBody @Valid RestaurantRequestDTO dto);

    @Operation(
        description = "Deletar um Restaurante",
        summary = "Endpoint responsável por receber a requisição de Deletar um Restaurante",
        responses = {
            @ApiResponse(description = "Restaurante deletado",responseCode = "204"),
            @ApiResponse(description = "Um usuário não cadastrado faz a requisição",responseCode = "401"),
            @ApiResponse(description = "Um usuário comum faz a requisição",responseCode = "403"),
            @ApiResponse(description = "Restaurante não encontrado",responseCode = "404"),
        }
    )
    ResponseEntity<Void> delete(@PathVariable String id);
    
}
