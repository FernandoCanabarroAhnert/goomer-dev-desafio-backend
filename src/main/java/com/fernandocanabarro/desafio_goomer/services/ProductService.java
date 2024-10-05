package com.fernandocanabarro.desafio_goomer.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fernandocanabarro.desafio_goomer.controllers.CategoryController;
import com.fernandocanabarro.desafio_goomer.controllers.RestaurantController;
import com.fernandocanabarro.desafio_goomer.models.category.Category;
import com.fernandocanabarro.desafio_goomer.models.category.CategoryDTO;
import com.fernandocanabarro.desafio_goomer.models.product.Product;
import com.fernandocanabarro.desafio_goomer.models.product.ProductRequestDTO;
import com.fernandocanabarro.desafio_goomer.models.product.ProductResponseDTO;
import com.fernandocanabarro.desafio_goomer.models.product.embedded.Offer;
import com.fernandocanabarro.desafio_goomer.models.product.embedded.OfferRequestDTO;
import com.fernandocanabarro.desafio_goomer.models.restaurant.Restaurant;
import com.fernandocanabarro.desafio_goomer.models.category.CategoryRepository;
import com.fernandocanabarro.desafio_goomer.models.product.ProductRepository;
import com.fernandocanabarro.desafio_goomer.models.restaurant.RestaurantRepository;
import com.fernandocanabarro.desafio_goomer.services.exceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final RestaurantRepository restaurantRepository;
    private final CategoryRepository categoryRepository;

    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyy HH:mm");

    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> findAll(Pageable pageable){
        Page<ProductResponseDTO> response = productRepository.findAll(pageable).map(x -> new ProductResponseDTO(x)
            .add(linkTo(methodOn(RestaurantController.class).findById(x.getRestaurantId())).withRel("Consultar Restaurante por Id")));
        for (ProductResponseDTO x : response){
            for (CategoryDTO cat : x.getCategories()){
                cat.add(linkTo(methodOn(CategoryController.class).findProductsByCategoryId(cat.getId(), null))
                    .withRel("Consultar Produtos da Categoria " + cat.getName()));
            }
        }
        return response;
    }

    @Transactional
    public ProductResponseDTO create(String restaurantId, ProductRequestDTO dto){
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
            .orElseThrow(() -> new ResourceNotFoundException("Restaurante não encontrado! Id = " + restaurantId));
        Product product = new Product();
        toEntity(product,dto);
        product.setRestaurantId(restaurantId);
        product = productRepository.save(product);
        restaurant.addProduct(product);
        restaurantRepository.save(restaurant);
        ProductResponseDTO response = new ProductResponseDTO(product);
        for (CategoryDTO cat : response.getCategories()){
            cat.add(linkTo(methodOn(CategoryController.class).findProductsByCategoryId(cat.getId(), null))
                .withRel("Consultar Produtos da Categoria " + cat.getName()));
        }
        return response
            .add(linkTo(methodOn(RestaurantController.class).findById(response.getRestaurantId())).withRel("Consultar Restaurante por Id"));
    }

    private void toEntity(Product entity,ProductRequestDTO dto) {
        entity.setName(dto.getName());
        entity.setImageUrl(dto.getImageUrl());
        entity.setPrice(dto.getPrice());
        for (CategoryDTO x : dto.getCategories()){
            Category category = categoryRepository.findById(x.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada! Id = " + x.getId()));
                entity.getCategories().add(category);
        }
        entity.setIsInOffer(false);
        entity.setOffer(null);
    }

    @Transactional
    public ProductResponseDTO update(String productId,ProductRequestDTO dto){
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado! Id = " + productId));
        product.getCategories().clear();
        Restaurant restaurant = restaurantRepository.findById(product.getRestaurantId()).get();
        restaurant.removeProduct(product);
        toEntity(product, dto);
        product = productRepository.save(product);
        restaurant.addProduct(product);
        restaurantRepository.save(restaurant);
        ProductResponseDTO response = new ProductResponseDTO(product);
        for (CategoryDTO cat : response.getCategories()){
            cat.add(linkTo(methodOn(CategoryController.class).findProductsByCategoryId(cat.getId(), null))
                .withRel("Consultar Produtos da Categoria " + cat.getName()));
        }
        return response
            .add(linkTo(methodOn(RestaurantController.class).findById(response.getRestaurantId())).withRel("Consultar Restaurante por Id"));
    }

    @Transactional
    public void delete(String productId){
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException(productId));
        Restaurant restaurant = restaurantRepository.findById(product.getRestaurantId()).get();
        restaurant.removeProduct(product);
        restaurantRepository.save(restaurant);
        productRepository.delete(product);
    }

    @Transactional
    public ProductResponseDTO createOffer(String productId,OfferRequestDTO offerRequestDTO){
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado! Id = " + productId));
        product.setIsInOffer(true);
        Offer offer = new Offer();
        offer.setDescription(offerRequestDTO.getDescription());
        offer.setOfferPrice(offerRequestDTO.getOfferPrice());
        offer.setOfferEndDate(LocalDateTime.parse(offerRequestDTO.getOfferEndDate(), dtf));
        product.setOffer(offer);
        product = productRepository.save(product);
        return new ProductResponseDTO(product)
            .add(linkTo(methodOn(RestaurantController.class).findById(product.getRestaurantId())).withRel("Consultar Restaurante por Id"));
    }

    @Scheduled(fixedRate = 30,timeUnit = TimeUnit.SECONDS)
    public void removeOfferFromProductsWithExpiredOffer(){
        List<Product> productsToBeUpdated = productRepository.findAll().stream().filter((x -> x.getIsInOffer()))
            .filter(x -> x.getOffer().getOfferEndDate().isBefore(LocalDateTime.now())).toList();
        for (Product x : productsToBeUpdated){
            x.setIsInOffer(false);
            x.setOffer(null);
            productRepository.save(x);
        }
    }
}
