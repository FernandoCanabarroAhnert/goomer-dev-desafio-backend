package com.fernandocanabarro.desafio_goomer.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fernandocanabarro.desafio_goomer.controllers.CategoryController;
import com.fernandocanabarro.desafio_goomer.controllers.RestaurantController;
import com.fernandocanabarro.desafio_goomer.models.category.CategoryDTO;
import com.fernandocanabarro.desafio_goomer.models.product.Product;
import com.fernandocanabarro.desafio_goomer.models.product.ProductResponseDTO;
import com.fernandocanabarro.desafio_goomer.models.restaurant.Restaurant;
import com.fernandocanabarro.desafio_goomer.models.restaurant.RestaurantRequestDTO;
import com.fernandocanabarro.desafio_goomer.models.restaurant.RestaurantResponseDTO;
import com.fernandocanabarro.desafio_goomer.models.user.User;
import com.fernandocanabarro.desafio_goomer.models.product.ProductRepository;
import com.fernandocanabarro.desafio_goomer.models.restaurant.RestaurantRepository;
import com.fernandocanabarro.desafio_goomer.services.exceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final AddressService addressService;
    private final ProductRepository productRepository;
    private final AuthService authService;

    @Transactional(readOnly = true)
    public Page<RestaurantResponseDTO> findAll(String tag,Pageable pageable){
        return (tag.isBlank())
            ? restaurantRepository.findAll(pageable).map(x -> new RestaurantResponseDTO(x)
                .add(linkTo(methodOn(RestaurantController.class).findById(x.getId())).withRel("Consultar Restaurante por Id")))
            : restaurantRepository.findByTag(tag, pageable).map(x -> new RestaurantResponseDTO(x)
                .add(linkTo(methodOn(RestaurantController.class).findById(x.getId())).withRel("Consultar Restaurante por Id")));
    }

    @Transactional(readOnly = true)
    public List<RestaurantResponseDTO> findByCoordinates(String tag,String longitude,String latitude,String maxDistance){
        if (maxDistance.isBlank()) {
            maxDistance = "10000";
        }
        return (tag.isBlank()) 
            ? restaurantRepository.findByCoordinates(Double.parseDouble(longitude), Double.parseDouble(latitude),Double.parseDouble(maxDistance))
            .stream().map(x -> new RestaurantResponseDTO(x).add(linkTo(methodOn(RestaurantController.class)
            .findById(x.getId())).withRel("Consultar Restaurante por Id"))).toList()
            : restaurantRepository.findByCoordinatesAndTag(tag,Double.parseDouble(longitude), Double.parseDouble(latitude),Double.parseDouble(maxDistance))
            .stream().map(x -> new RestaurantResponseDTO(x).add(linkTo(methodOn(RestaurantController.class)
            .findById(x.getId())).withRel("Consultar Restaurante por Id"))).toList();
    }

    @Transactional(readOnly = true)
    public List<RestaurantResponseDTO> findByCoordinatesNearMe(String tag,String maxDistance){
        User user = authService.getConnectedUser();
        Double longitude = user.getGeoPoint().getX();
        Double latitude = user.getGeoPoint().getY();
        if (maxDistance.isBlank()) {
            maxDistance = "10000";
        }
        return (tag.isBlank()) 
        ? restaurantRepository.findByCoordinates(longitude,latitude,Double.parseDouble(maxDistance))
            .stream().map(x -> new RestaurantResponseDTO(x).add(linkTo(methodOn(RestaurantController.class)
            .findById(x.getId())).withRel("Consultar Restaurante por Id"))).toList()
        : restaurantRepository.findByCoordinatesAndTag(tag,longitude, latitude,Double.parseDouble(maxDistance))
            .stream().map(x -> new RestaurantResponseDTO(x).add(linkTo(methodOn(RestaurantController.class)
            .findById(x.getId())).withRel("Consultar Restaurante por Id"))).toList();
    }

    @Transactional(readOnly = true)
    public RestaurantResponseDTO findById(String id){
        Restaurant restaurant = restaurantRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Restaurante não encontrado! Id = " + id));
        return new RestaurantResponseDTO(restaurant).add(linkTo(methodOn(RestaurantController.class)
            .findProductsByRestaurantId(null, restaurant.getId())).withRel("Consultar Produtos do Restaurante " + restaurant.getName()));
    }

    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> findAllProductsByRestaurantId(String restaurantId,Pageable pageable){
        restaurantRepository.findById(restaurantId)
            .orElseThrow(() -> new ResourceNotFoundException("Restaurante não encontrado! Id = " + restaurantId));
        Page<ProductResponseDTO> response = productRepository.findByRestaurantId(restaurantId, pageable).map(x -> new ProductResponseDTO(x)
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
    public RestaurantResponseDTO create(RestaurantRequestDTO dto){
        Restaurant restaurant = new Restaurant();
        toEntity(restaurant,dto);
        restaurant.setMenu(new ArrayList<>(Arrays.asList()));
        restaurant = restaurantRepository.save(restaurant);
        return new RestaurantResponseDTO(restaurant)
            .add(linkTo(methodOn(RestaurantController.class).findById(restaurant.getId())).withRel("Consultar Restaurante por Id"));
    }

    private void toEntity(Restaurant entity,RestaurantRequestDTO dto) {
        entity.setName(dto.getName());
        entity.setImageUrl(dto.getImageUrl());
        entity.setAddress(addressService.getAddressFromRequest(dto.getAddress()));
        entity.setGeoPoint(new GeoJsonPoint(dto.getCoordinates().getLongitude(), dto.getCoordinates().getLatitude()));
        entity.setOpeningHours(dto.getOpeningHours());
        entity.setTags(dto.getTags());
    }

    @Transactional
    public RestaurantResponseDTO update(String restaurantId,RestaurantRequestDTO dto){
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
            .orElseThrow(() -> new ResourceNotFoundException("Restaurante não encontrado! Id = " + restaurantId));
        restaurant.getTags().clear();
        restaurant.getOpeningHours().clear();
        toEntity(restaurant, dto);
        restaurant = restaurantRepository.save(restaurant);
        return new RestaurantResponseDTO(restaurant)
            .add(linkTo(methodOn(RestaurantController.class).findById(restaurant.getId())).withRel("Consultar Restaurante por Id"));
    }

    @Transactional
    public void delete(String restaurantId){
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
            .orElseThrow(() -> new ResourceNotFoundException("Restaurante não encontrado! Id = " + restaurantId));
        for (Product product : restaurant.getMenu()){
            productRepository.delete(product);
        }
        restaurantRepository.delete(restaurant);
    }
   
}
