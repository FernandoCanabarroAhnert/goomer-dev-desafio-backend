package com.fernandocanabarro.desafio_goomer.openapi;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.fernandocanabarro.desafio_goomer.models.category.CategoryDTO;
import com.fernandocanabarro.desafio_goomer.models.product.ProductResponseDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

import java.util.List;

public interface CategoryControllerOpenAPI {

    @Operation(
        description = "Consultar todas as categorias",
        summary = "Endpoint responsável por receber a requisição de Consultar todas as categorias",
        responses = {
            @ApiResponse(description = "Consulta realizada",responseCode = "200"),
            @ApiResponse(description = "Um usuário não cadastrado faz a requisição",responseCode = "401")
        }
    )
    ResponseEntity<List<CategoryDTO>> findAll();

    @Operation(
        description = "Consultar todos os Produtos de uma categoria",
        summary = "Endpoint responsável por receber a requisição de Consultar todos os Produtos de uma categoria",
        responses = {
            @ApiResponse(description = "Consulta realizada",responseCode = "200"),
            @ApiResponse(description = "Um usuário não cadastrado faz a requisição",responseCode = "401"),
            @ApiResponse(description = "Categoria não encontrada",responseCode = "404")
        }
    )
    ResponseEntity<Page<ProductResponseDTO>> findProductsByCategoryId(@PathVariable String id,Pageable pageable);

    @Operation(
        description = "Criar uma nova categoria",
        summary = "Endpoint responsável por receber a requisição de Criar uma nova categoria",
        responses = {
            @ApiResponse(description = "Categoria criada",responseCode = "201"),
            @ApiResponse(description = "Um usuário não cadastrado faz a requisição",responseCode = "401"),
            @ApiResponse(description = "Um usuário comum faz a requisição",responseCode = "403"),
            @ApiResponse(description = "Algum dado da requisição está inválido",responseCode = "422")
        }
    )
    ResponseEntity<CategoryDTO> create(@RequestBody @Valid CategoryDTO dto);
    
}
