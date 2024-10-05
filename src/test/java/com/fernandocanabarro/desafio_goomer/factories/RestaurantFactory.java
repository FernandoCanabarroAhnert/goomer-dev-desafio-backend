package com.fernandocanabarro.desafio_goomer.factories;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import com.fernandocanabarro.desafio_goomer.models.restaurant.Restaurant;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class RestaurantFactory {

    public static Restaurant getRestaurant(){
        Map<String,String> openingHours = new HashMap<>();
        openingHours.put("Segunda a Sexta","11h-23h");
        openingHours.put("Sábado e Domingo","11h-21h");
        List<String> tags = new ArrayList<>(Arrays.asList("tag"));
        return new Restaurant("1", "name", "imageUrl", AddressFactory.getAddress(), 
            new GeoJsonPoint(1.0,1.0), openingHours, tags, new ArrayList<>(Arrays.asList(ProductFactory.getProduct())));
    }
}
