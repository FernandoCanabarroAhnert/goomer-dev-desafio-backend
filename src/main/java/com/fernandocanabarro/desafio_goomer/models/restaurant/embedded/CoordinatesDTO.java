package com.fernandocanabarro.desafio_goomer.models.restaurant.embedded;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CoordinatesDTO {

    @NotBlank(message = "Campo Requerido")
    private Double longitude;
    @NotBlank(message = "Campo Requerido")
    private Double latitude;
}
