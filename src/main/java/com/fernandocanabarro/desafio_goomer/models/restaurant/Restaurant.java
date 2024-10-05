package com.fernandocanabarro.desafio_goomer.models.restaurant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fernandocanabarro.desafio_goomer.models.product.Product;
import com.fernandocanabarro.desafio_goomer.models.restaurant.embedded.Address;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "restaurants")
public class Restaurant {

    @Id
    private String id;
    private String name;
    private String imageUrl;
    private Address address;
    @GeoSpatialIndexed
    @Field("geoPoint")
    private GeoJsonPoint geoPoint;
    private Map<String,String> openingHours = new HashMap<>();
    private List<String> tags;
    @DBRef(lazy = true)
    private List<Product> menu = new ArrayList<>();
    
    public void addProduct(Product product){
        menu.add(product);
    }

    public void removeProduct(Product product){
        menu.remove(product);
    }
}
