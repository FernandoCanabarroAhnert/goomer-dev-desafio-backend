package com.fernandocanabarro.desafio_goomer.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fernandocanabarro.desafio_goomer.controllers.CategoryController;
import com.fernandocanabarro.desafio_goomer.controllers.RestaurantController;
import com.fernandocanabarro.desafio_goomer.models.category.Category;
import com.fernandocanabarro.desafio_goomer.models.category.CategoryDTO;
import com.fernandocanabarro.desafio_goomer.models.product.ProductResponseDTO;
import com.fernandocanabarro.desafio_goomer.models.category.CategoryRepository;
import com.fernandocanabarro.desafio_goomer.models.product.ProductRepository;
import com.fernandocanabarro.desafio_goomer.services.exceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Transactional
    public CategoryDTO create(CategoryDTO dto){
        Category category = new Category();
        category.setName(dto.getName());
        category = categoryRepository.save(category);
        return new CategoryDTO(category).add(linkTo(methodOn(CategoryController.class)
            .findProductsByCategoryId(category.getId(), null))
            .withRel("Consultar Produtos da Categoria " + category.getName()));
    }

    @Transactional(readOnly = true)
    public List<CategoryDTO> findAll(){
        return categoryRepository.findAll().stream().map(x -> new CategoryDTO(x)
            .add(linkTo(methodOn(CategoryController.class).findProductsByCategoryId(x.getId(), null))
            .withRel("Consultar Produtos da Categoria " + x.getName())))
            .toList();
    }

    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> findProductsByCategoryId(String categoryId,Pageable pageable){
        categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException("Categoria não Encontrada! Id = " + categoryId));
        return productRepository.findByCategoryId(categoryId,pageable).map(x -> new ProductResponseDTO(x)
            .add(linkTo(methodOn(RestaurantController.class).findById(x.getRestaurantId())).withRel("Consultar Restaurante por Id")));
    }
}
