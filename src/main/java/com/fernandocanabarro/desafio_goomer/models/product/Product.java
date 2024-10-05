package com.fernandocanabarro.desafio_goomer.models.product;

import java.util.List;
import java.util.ArrayList;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fernandocanabarro.desafio_goomer.models.category.Category;
import com.fernandocanabarro.desafio_goomer.models.product.embedded.Offer;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "products")
@EqualsAndHashCode(of = "id")
public class Product {

    @Id
    private String id;
    private String name;
    private Double price; 
    private String imageUrl;
    private List<Category> categories = new ArrayList<>();
    private String restaurantId;
    private Boolean isInOffer;
    private Offer offer;
}
