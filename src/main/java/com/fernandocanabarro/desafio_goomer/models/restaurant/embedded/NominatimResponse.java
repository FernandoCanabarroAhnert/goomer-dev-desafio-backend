package com.fernandocanabarro.desafio_goomer.models.restaurant.embedded;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NominatimResponse {

    private String lat;
    private String lon;
}
