package com.fernandocanabarro.desafio_goomer.models.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends MongoRepository<Product,String>{

    Page<Product> findByRestaurantId(String restaurantId,Pageable pageable);

    @Query("{ 'categories.id': ?0 }")
    Page<Product> findByCategoryId(String categoryId,Pageable pageable);
}
