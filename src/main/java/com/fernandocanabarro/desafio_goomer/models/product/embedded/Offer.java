package com.fernandocanabarro.desafio_goomer.models.product.embedded;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Offer {

    private String description;
    private Double offerPrice;
    private LocalDateTime offerEndDate;
}
