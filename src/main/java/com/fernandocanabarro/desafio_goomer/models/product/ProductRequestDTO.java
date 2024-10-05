package com.fernandocanabarro.desafio_goomer.models.product;

import java.util.List;

import com.fernandocanabarro.desafio_goomer.models.category.CategoryDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequestDTO {

    @NotBlank(message = "Campo Requerido")
    private String name;
    @Positive(message = "Valor deve ser positivo")
    private Double price;
    @NotBlank(message = "Campo Requerido")
    private String imageUrl;
    @NotEmpty(message = "Deve haver pelo menos 1 categoria")
    private List<CategoryDTO> categories;
}
