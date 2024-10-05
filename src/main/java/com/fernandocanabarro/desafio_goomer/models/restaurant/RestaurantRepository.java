package com.fernandocanabarro.desafio_goomer.models.restaurant;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantRepository extends MongoRepository<Restaurant,String>{

    @Query("{ tags: { $regex: ?0, $options: i } }")
    Page<Restaurant> findByTag(String tag,Pageable pageable);

    @Query("{ geoPoint: { $near: { $geometry: { type: 'Point', coordinates: [ ?0, ?1 ] }, $maxDistance: ?2 } } }")
    List<Restaurant> findByCoordinates(Double longitude,Double latitude,Double maxDistance);

    @Query("{ tags: { $regex: ?0, $options: i }, geoPoint: { $near: { $geometry: { type: 'Point', coordinates: [ ?1, ?2 ] }, $maxDistance: ?3 } } }")
    List<Restaurant> findByCoordinatesAndTag(String tag,Double longitude,Double latitude,Double maxDistance);
}
