package com.fernandocanabarro.desafio_goomer.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.fernandocanabarro.desafio_goomer.factories.AddressFactory;
import com.fernandocanabarro.desafio_goomer.factories.ProductFactory;
import com.fernandocanabarro.desafio_goomer.factories.RestaurantFactory;
import com.fernandocanabarro.desafio_goomer.factories.UserFactory;
import com.fernandocanabarro.desafio_goomer.models.product.Product;
import com.fernandocanabarro.desafio_goomer.models.product.ProductResponseDTO;
import com.fernandocanabarro.desafio_goomer.models.restaurant.Restaurant;
import com.fernandocanabarro.desafio_goomer.models.restaurant.RestaurantRequestDTO;
import com.fernandocanabarro.desafio_goomer.models.restaurant.RestaurantResponseDTO;
import com.fernandocanabarro.desafio_goomer.models.restaurant.embedded.Address;
import com.fernandocanabarro.desafio_goomer.models.restaurant.embedded.AddressRequestDTO;
import com.fernandocanabarro.desafio_goomer.models.restaurant.embedded.CoordinatesDTO;
import com.fernandocanabarro.desafio_goomer.models.user.User;
import com.fernandocanabarro.desafio_goomer.models.product.ProductRepository;
import com.fernandocanabarro.desafio_goomer.models.restaurant.RestaurantRepository;
import com.fernandocanabarro.desafio_goomer.services.exceptions.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
public class RestaurantServiceTests {

    @InjectMocks
    private RestaurantService restaurantService;
    @Mock
    private RestaurantRepository restaurantRepository;
    @Mock
    private AddressService addressService;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private AuthService authService;

    private String existingId,nonExistingId;
    private String tag;
    private Restaurant restaurant;
    private Pageable pageable;
    private Page<Restaurant> restaurantPage;
    private Address address;
    private AddressRequestDTO addressRequestDTO;
    private RestaurantRequestDTO restaurantRequestDTO;
    private User user;
    private Product product;
    private Page<Product> productPage;

    @BeforeEach
    public void setup() throws Exception{
        existingId = "1";
        nonExistingId = "2";
        tag = "tag";
        restaurant = RestaurantFactory.getRestaurant();
        pageable = PageRequest.of(0, 10);
        restaurantPage = new PageImpl<>(new ArrayList<>(Arrays.asList(restaurant)));

        address = AddressFactory.getAddress();
        addressRequestDTO = new AddressRequestDTO("cep", "numero", "complemento");

        Map<String,String> openingHours = Map.of(
            "Segunda a Sexta","11h-23h",
            "Sábado e Domingo","11h-21h"
        );
        List<String> tags = new ArrayList<>(Arrays.asList(tag));
        restaurantRequestDTO = new RestaurantRequestDTO("name", "imageUrl", addressRequestDTO,
             new CoordinatesDTO(1.0, 1.0), openingHours, tags);
        user = UserFactory.getUser();
        product = ProductFactory.getProduct();
        productPage = new PageImpl<>(new ArrayList<>(Arrays.asList(product)));
    }

    @Test
    public void findAllShouldReturnPageOfRestaurantResponseDTOWhenTagIsNotBlank(){
        when(restaurantRepository.findByTag(anyString(), any(Pageable.class))).thenReturn(restaurantPage);

        Page<RestaurantResponseDTO> response = restaurantService.findAll(tag, pageable);

        assertThat(response).isNotEmpty();
        assertThat(response.getContent().get(0).getName()).isEqualTo("name");
        assertThat(response.getContent().get(0).getImageUrl()).isEqualTo("imageUrl");
        assertThat(response.getContent().get(0).getAddress().getLogradouro()).isEqualTo("logradouro");
        assertThat(response.getContent().get(0).getPoint().getLatitude()).isEqualTo(1.0);
    }

    @Test
    public void findAllShouldReturnPageOfRestaurantResponseDTOWhenTagIsBlank(){
        when(restaurantRepository.findAll(any(Pageable.class))).thenReturn(restaurantPage);

        Page<RestaurantResponseDTO> response = restaurantService.findAll("", pageable);

        assertThat(response).isNotEmpty();
        assertThat(response.getContent().get(0).getName()).isEqualTo("name");
        assertThat(response.getContent().get(0).getImageUrl()).isEqualTo("imageUrl");
        assertThat(response.getContent().get(0).getAddress().getLogradouro()).isEqualTo("logradouro");
        assertThat(response.getContent().get(0).getPoint().getLatitude()).isEqualTo(1.0);
    }

    @Test
    public void findByCoordinatesShouldReturnListOfRestaurantResponseDTOWhenTagIsNotBlank(){
        when(restaurantRepository.findByCoordinatesAndTag(tag, 1.0, 1.0, 10000.0))
            .thenReturn(new ArrayList<>(List.of(restaurant)));
        
        List<RestaurantResponseDTO> response = restaurantService.findByCoordinates(tag, "1", "1", "");

        assertThat(response).isNotEmpty();
        assertThat(response.get(0).getName()).isEqualTo("name");
        assertThat(response.get(0).getImageUrl()).isEqualTo("imageUrl");
        assertThat(response.get(0).getAddress().getLogradouro()).isEqualTo("logradouro");
        assertThat(response.get(0).getPoint().getLatitude()).isEqualTo(1.0);   
    }

    @Test
    public void findByCoordinatesShouldReturnListOfRestaurantResponseDTOWhenTagIsBlank(){
        when(restaurantRepository.findByCoordinates(1.0, 1.0, 1000.0))
            .thenReturn(new ArrayList<>(List.of(restaurant)));
        
        List<RestaurantResponseDTO> response = restaurantService.findByCoordinates("", "1", "1", "1000");

        assertThat(response).isNotEmpty();
        assertThat(response.get(0).getName()).isEqualTo("name");
        assertThat(response.get(0).getImageUrl()).isEqualTo("imageUrl");
        assertThat(response.get(0).getAddress().getLogradouro()).isEqualTo("logradouro");
        assertThat(response.get(0).getPoint().getLatitude()).isEqualTo(1.0);   
    }

    @Test
    public void findByCoordinatesNearMeShouldReturnListOfRestaurantResponseDTOWhenTagIsNotBlank(){
        when(authService.getConnectedUser()).thenReturn(user);
        when(restaurantRepository.findByCoordinatesAndTag(tag, 1.0, 1.0, 10000.0))
            .thenReturn(new ArrayList<>(List.of(restaurant)));
        
        List<RestaurantResponseDTO> response = restaurantService.findByCoordinatesNearMe(tag, "");

        assertThat(response).isNotEmpty();
        assertThat(response.get(0).getName()).isEqualTo("name");
        assertThat(response.get(0).getImageUrl()).isEqualTo("imageUrl");
        assertThat(response.get(0).getAddress().getLogradouro()).isEqualTo("logradouro");
        assertThat(response.get(0).getPoint().getLatitude()).isEqualTo(1.0);   
    }

    @Test
    public void findByCoordinatesNearShouldReturnListOfRestaurantResponseDTOWhenTagIsBlank(){
        when(authService.getConnectedUser()).thenReturn(user);
        when(restaurantRepository.findByCoordinates(1.0, 1.0, 1000.0))
            .thenReturn(new ArrayList<>(List.of(restaurant)));
        
        List<RestaurantResponseDTO> response = restaurantService.findByCoordinatesNearMe("", "1000");

        assertThat(response).isNotEmpty();
        assertThat(response.get(0).getName()).isEqualTo("name");
        assertThat(response.get(0).getImageUrl()).isEqualTo("imageUrl");
        assertThat(response.get(0).getAddress().getLogradouro()).isEqualTo("logradouro");
        assertThat(response.get(0).getPoint().getLatitude()).isEqualTo(1.0);   
    }

    @Test
    public void findByIdShouldReturnRestaurantResponseDTOWhenRestaurantExists(){
        when(restaurantRepository.findById(existingId)).thenReturn(Optional.of(restaurant));

        RestaurantResponseDTO response = restaurantService.findById(existingId);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("name");
        assertThat(response.getImageUrl()).isEqualTo("imageUrl");
        assertThat(response.getAddress().getLogradouro()).isEqualTo("logradouro");
        assertThat(response.getPoint().getLatitude()).isEqualTo(1.0);   
    }

    @Test
    public void findByIdShouldThrowResourceNotFoundExceptionWhenRestaurantDoesNotExist(){
        when(restaurantRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> restaurantService.findById(nonExistingId)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void findAllProductsByRestaurantIdShouldReturnPageOfProductResponseDTOWhenRestaurantExists(){
        when(restaurantRepository.findById(existingId)).thenReturn(Optional.of(restaurant));
        when(productRepository.findByRestaurantId(existingId, pageable)).thenReturn(productPage);
        
        Page<ProductResponseDTO> response = restaurantService.findAllProductsByRestaurantId(existingId,pageable);

        assertThat(response).isNotEmpty();
        assertThat(response.getContent().get(0).getName()).isEqualTo("name");
        assertThat(response.getContent().get(0).getPrice()).isEqualTo(10.0);
        assertThat(response.getContent().get(0).getImageUrl()).isEqualTo("imageUrl");
    }

    @Test
    public void findAllProductsByRestaurantIdShouldThrowResourceNotFoundExceptionWhenRestaurantDoesNotExist(){
        when(restaurantRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> restaurantService.findAllProductsByRestaurantId(nonExistingId,pageable)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void createShouldReturnRestaurantResponseDTO(){
        when(addressService.getAddressFromRequest(addressRequestDTO)).thenReturn(address);
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(restaurant);

        RestaurantResponseDTO response = restaurantService.create(restaurantRequestDTO);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("name");
        assertThat(response.getImageUrl()).isEqualTo("imageUrl");
        assertThat(response.getAddress().getLogradouro()).isEqualTo("logradouro");
        assertThat(response.getPoint().getLatitude()).isEqualTo(1.0);   
    }

    @Test
    public void updateShouldReturnRestaurantResponseDTOWhenRestaurantExists(){
        when(restaurantRepository.findById(existingId)).thenReturn(Optional.of(restaurant));
        when(addressService.getAddressFromRequest(addressRequestDTO)).thenReturn(address);
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(restaurant);

        RestaurantResponseDTO response = restaurantService.update(existingId,restaurantRequestDTO);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("name");
        assertThat(response.getImageUrl()).isEqualTo("imageUrl");
        assertThat(response.getAddress().getLogradouro()).isEqualTo("logradouro");
        assertThat(response.getPoint().getLatitude()).isEqualTo(1.0);  
    }

    @Test
    public void updateShouldThrowResourceNotFoundExceptionWhenRestaurantDoesNotExist(){
        when(restaurantRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> restaurantService.update(nonExistingId,restaurantRequestDTO)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void deleteShouldThrowResourceNoExceptionWhenRestaurantExists(){
        when(restaurantRepository.findById(existingId)).thenReturn(Optional.of(restaurant));
        doNothing().when(restaurantRepository).delete(any(Restaurant.class));

        assertThatCode(() -> restaurantService.delete(existingId)).doesNotThrowAnyException();
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenRestaurantDoesNotExist(){
        when(restaurantRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> restaurantService.delete(nonExistingId)).isInstanceOf(ResourceNotFoundException.class);
    }
}
