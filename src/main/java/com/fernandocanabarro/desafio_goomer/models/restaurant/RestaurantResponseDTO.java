package com.fernandocanabarro.desafio_goomer.models.restaurant;

import java.util.List;
import java.util.Map;

import org.springframework.hateoas.RepresentationModel;

import com.fernandocanabarro.desafio_goomer.models.restaurant.embedded.Address;
import com.fernandocanabarro.desafio_goomer.models.restaurant.embedded.CoordinatesDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantResponseDTO extends RepresentationModel<RestaurantResponseDTO>{

    private String id;
    private String name;
    private String imageUrl;
    private Address address;
    private CoordinatesDTO point;
    private Map<String,String> openingHours;
    private List<String> tags;

    public RestaurantResponseDTO(Restaurant entity){
        id = entity.getId();
        name = entity.getName();
        imageUrl = entity.getImageUrl();
        address = entity.getAddress();
        point = new CoordinatesDTO(entity.getGeoPoint().getX(), entity.getGeoPoint().getY());
        openingHours = entity.getOpeningHours();
        tags = entity.getTags();
    }
}
