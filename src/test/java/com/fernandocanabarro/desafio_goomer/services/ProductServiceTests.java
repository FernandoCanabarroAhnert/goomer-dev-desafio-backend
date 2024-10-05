package com.fernandocanabarro.desafio_goomer.services;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fernandocanabarro.desafio_goomer.factories.CategoryFactory;
import com.fernandocanabarro.desafio_goomer.factories.ProductFactory;
import com.fernandocanabarro.desafio_goomer.factories.RestaurantFactory;
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

@ExtendWith(MockitoExtension.class)
public class ProductServiceTests {

    @InjectMocks
    private ProductService productService;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private RestaurantRepository restaurantRepository;
    @Mock
    private CategoryRepository categoryRepository;

    private String existingId,nonExistingId;
    private Product product;
    private ProductRequestDTO requestDTO;
    private Restaurant restaurant;
    private Pageable pageable;
    private Page<Product> page;
    private Category category;
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyy HH:mm");
    private OfferRequestDTO offerRequestDTO;

    @BeforeEach
    public void setup() throws Exception{
        existingId = "1";
        nonExistingId = "2";
        product = ProductFactory.getProduct();
        requestDTO = new ProductRequestDTO("name", 10.0, "imageUrl", List.of(new CategoryDTO(CategoryFactory.getCategory())));
        restaurant = RestaurantFactory.getRestaurant();
        pageable = PageRequest.of(0, 10);
        page = new PageImpl<>(List.of(product));
        category = CategoryFactory.getCategory();
        offerRequestDTO = new OfferRequestDTO("description", 5.0,
            LocalDateTime.now().minusMinutes(1L).format(dtf));
    }

    @Test
    public void findAllShouldReturnPageOfProductResponseDTO(){
        when(productRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<ProductResponseDTO> response = productService.findAll(pageable);

        assertThat(response).isNotEmpty();
        assertThat(response.getContent().get(0).getName()).isEqualTo("name");
        assertThat(response.getContent().get(0).getPrice()).isEqualTo(10.0);
        assertThat(response.getContent().get(0).getImageUrl()).isEqualTo("imageUrl");
    }

    @Test
    public void createProductShouldReturnProductResponseDTOWhenRestaurantExists(){
        when(restaurantRepository.findById(existingId)).thenReturn(Optional.of(restaurant));
        when(categoryRepository.findById(existingId)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(restaurant);

        ProductResponseDTO response = productService.create(existingId, requestDTO);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("name");
        assertThat(response.getImageUrl()).isEqualTo("imageUrl");
        assertThat(response.getPrice()).isEqualTo(10.0);
    }

    @Test
    public void createProductShouldReturnThrowResourceNotFoundExceptionWhenRestaurantDoesNotExist(){
        when(restaurantRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.create(nonExistingId, requestDTO)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void createProductShouldReturnThrowResourceNotFoundExceptionWhenCategoryDoesNotExist(){
        requestDTO.getCategories().get(0).setId(nonExistingId);
        when(restaurantRepository.findById(existingId)).thenReturn(Optional.of(restaurant));
        when(categoryRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.create(existingId, requestDTO)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void updateProductShouldReturnProductResponseDTOWhenProductExists(){
        when(productRepository.findById(existingId)).thenReturn(Optional.of(product));
        when(restaurantRepository.findById(existingId)).thenReturn(Optional.of(restaurant));
        when(categoryRepository.findById(existingId)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(restaurant);

        ProductResponseDTO response = productService.update(existingId, requestDTO);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("name");
        assertThat(response.getImageUrl()).isEqualTo("imageUrl");
        assertThat(response.getPrice()).isEqualTo(10.0);
    }

    @Test
    public void updateProductShouldReturnThrowResourceNotFoundExceptionWhenProductDoesNotExist(){
        when(productRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.update(nonExistingId, requestDTO)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void updateProductShouldReturnThrowResourceNotFoundExceptionWhenCategoryDoesNotExist(){
        requestDTO.getCategories().get(0).setId(nonExistingId);
        when(productRepository.findById(existingId)).thenReturn(Optional.of(product));
        when(restaurantRepository.findById(existingId)).thenReturn(Optional.of(restaurant));
        when(categoryRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.update(existingId, requestDTO)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void deleteShouldThrowNotExceptionWhenProductExists(){
        when(productRepository.findById(existingId)).thenReturn(Optional.of(product));
        when(restaurantRepository.findById(existingId)).thenReturn(Optional.of(restaurant));
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(restaurant);
        doNothing().when(productRepository).delete(any(Product.class));

        assertThatCode(() -> productService.delete(existingId)).doesNotThrowAnyException();
    }

    @Test
    public void deleteProductShouldReturnThrowResourceNotFoundExceptionWhenProductDoesNotExist(){
        when(productRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.delete(nonExistingId)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void createOfferShouldReturnProductResponseDTOWhenProductExists(){
        when(productRepository.findById(existingId)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        
        ProductResponseDTO response = productService.createOffer(existingId, offerRequestDTO);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("name");
        assertThat(response.getImageUrl()).isEqualTo("imageUrl");
        assertThat(response.getPrice()).isEqualTo(10.0);
    }

    @Test
    public void createOfferProductShouldReturnThrowResourceNotFoundExceptionWhenProductDoesNotExist(){
        when(productRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.createOffer(nonExistingId, offerRequestDTO)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void removeOfferFromProductsWithExpiredOfferShouldThrowNoException(){
        product.setIsInOffer(true);
        Offer offer = new Offer("description", 5.0, LocalDateTime.now().minusMinutes(1L));
        product.setOffer(offer);
        when(productRepository.findAll()).thenReturn(List.of(product));

        assertThatCode(() -> productService.removeOfferFromProductsWithExpiredOffer()).doesNotThrowAnyException();
    }
}
