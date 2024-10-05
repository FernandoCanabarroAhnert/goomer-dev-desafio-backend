package com.fernandocanabarro.desafio_goomer.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

import com.fernandocanabarro.desafio_goomer.factories.CategoryFactory;
import com.fernandocanabarro.desafio_goomer.factories.ProductFactory;
import com.fernandocanabarro.desafio_goomer.models.category.Category;
import com.fernandocanabarro.desafio_goomer.models.category.CategoryDTO;
import com.fernandocanabarro.desafio_goomer.models.product.Product;
import com.fernandocanabarro.desafio_goomer.models.product.ProductResponseDTO;
import com.fernandocanabarro.desafio_goomer.models.category.CategoryRepository;
import com.fernandocanabarro.desafio_goomer.models.product.ProductRepository;
import com.fernandocanabarro.desafio_goomer.services.exceptions.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTests {

    @InjectMocks
    private CategoryService categoryService;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ProductRepository productRepository;

    private String existingId,nonExistingId;
    private Category category;
    private CategoryDTO categoryDTO;
    private Product product;

    @BeforeEach
    public void setup() throws Exception{
        existingId = "1";
        nonExistingId = "2";
        category = CategoryFactory.getCategory();
        categoryDTO = new CategoryDTO(category);
        product = ProductFactory.getProduct();
    }

    @Test
    public void createShouldReturnCategoryDTO(){
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryDTO response = categoryService.create(categoryDTO);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("1");
        assertThat(response.getName()).isEqualTo("name");
    }

    @Test
    public void findAllShoulReturnListOfCategoryDTO(){
        when(categoryRepository.findAll()).thenReturn(List.of(category));

        List<CategoryDTO> response = categoryService.findAll();

        assertThat(response).isNotEmpty();
        assertThat(response.get(0).getId()).isEqualTo("1");
        assertThat(response.get(0).getName()).isEqualTo("name");
    }

    @Test
    public void findProductsByCategoryIdShouldReturnPageOfProductResponseDTOWhenCategoryExists(){
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = new PageImpl<>(List.of(product));
        when(categoryRepository.findById(existingId)).thenReturn(Optional.of(category));
        when(productRepository.findByCategoryId(existingId, pageable)).thenReturn(page);

        Page<ProductResponseDTO> response = categoryService.findProductsByCategoryId(existingId, pageable);

        assertThat(response).isNotEmpty();
        assertThat(response.getContent().get(0).getId()).isEqualTo("1");
        assertThat(response.getContent().get(0).getName()).isEqualTo("name");
        assertThat(response.getContent().get(0).getPrice()).isEqualTo(10.0);
        assertThat(response.getContent().get(0).getImageUrl()).isEqualTo("imageUrl");
    }

    @Test
    public void findProductsByCategoryIdShouldThrowResourceNotFoundWhenCategoryDoesNotExist(){
        Pageable pageable = PageRequest.of(0, 10);
        when(categoryRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.findProductsByCategoryId(nonExistingId, pageable)).isInstanceOf(ResourceNotFoundException.class);
    }
}
