package com.fernandocanabarro.desafio_goomer.models.product.embedded;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OfferRequestDTO {

    @NotBlank(message = "Campo Requerido")
    private String description;
    @Positive(message = "Valor deve ser positivo")
    private Double offerPrice;
    @NotBlank(message = "Campo Requerido")
    private String offerEndDate;
}
