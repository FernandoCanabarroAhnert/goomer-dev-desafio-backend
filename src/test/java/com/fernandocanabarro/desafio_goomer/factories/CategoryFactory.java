package com.fernandocanabarro.desafio_goomer.factories;

import com.fernandocanabarro.desafio_goomer.models.category.Category;

public class CategoryFactory {

    public static Category getCategory(){
        return new Category("1", "name");
    }
}
