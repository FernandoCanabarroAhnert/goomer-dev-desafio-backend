package com.fernandocanabarro.desafio_goomer.factories;

import java.util.ArrayList;
import java.util.Arrays;

import com.fernandocanabarro.desafio_goomer.models.product.Product;

public class ProductFactory {

    public static Product getProduct(){
        return new Product("1", "name", 10.0, "imageUrl", new ArrayList<>(Arrays.asList((CategoryFactory.getCategory()))), "1", false, null);
    }
}
