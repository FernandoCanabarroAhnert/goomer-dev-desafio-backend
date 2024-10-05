package com.fernandocanabarro.desafio_goomer.models.restaurant;

import java.util.List;
import java.util.Map;

import com.fernandocanabarro.desafio_goomer.models.restaurant.embedded.AddressRequestDTO;
import com.fernandocanabarro.desafio_goomer.models.restaurant.embedded.CoordinatesDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantRequestDTO {

    @NotBlank(message = "Campo Requerido")
    private String name;
    @NotBlank(message = "Campo Requerido")
    private String imageUrl;
    private AddressRequestDTO address;
    private CoordinatesDTO coordinates;
    @NotEmpty(message = "Deve haver os dias e horários de funcionamento")
    private Map<String,String> openingHours;
    @NotEmpty(message = "Deve haver pelo menos 1 tag")
    private List<String> tags;
}
