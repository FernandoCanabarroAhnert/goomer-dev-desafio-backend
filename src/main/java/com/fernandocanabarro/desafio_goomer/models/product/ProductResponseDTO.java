package com.fernandocanabarro.desafio_goomer.models.product;

import java.util.List;

import org.springframework.hateoas.RepresentationModel;

import com.fernandocanabarro.desafio_goomer.models.category.CategoryDTO;
import com.fernandocanabarro.desafio_goomer.models.product.embedded.Offer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseDTO extends RepresentationModel<ProductResponseDTO>{

    private String id;
    private String name;
    private Double price;
    private String imageUrl;
    private List<CategoryDTO> categories;
    private String restaurantId;
    private Boolean isInOffer;
    private Offer offer;

    public ProductResponseDTO(Product entity){
        id = entity.getId();
        name = entity.getName();
        price = entity.getPrice();
        imageUrl = entity.getImageUrl();
        categories = entity.getCategories().stream().map(CategoryDTO::new).toList();
        restaurantId = entity.getRestaurantId();
        isInOffer = entity.getIsInOffer();
        offer = entity.getOffer();
    }
}
