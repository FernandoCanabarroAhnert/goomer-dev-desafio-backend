package com.fernandocanabarro.desafio_goomer.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fernandocanabarro.desafio_goomer.models.product.ProductResponseDTO;
import com.fernandocanabarro.desafio_goomer.models.restaurant.RestaurantRequestDTO;
import com.fernandocanabarro.desafio_goomer.models.restaurant.RestaurantResponseDTO;
import com.fernandocanabarro.desafio_goomer.services.RestaurantService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/restaurants")
public class RestaurantController {

    @Autowired
    private RestaurantService service;

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<Page<RestaurantResponseDTO>> findAll(Pageable pageable, @RequestParam(name = "tag",defaultValue = "") String tag){
        return ResponseEntity.ok(service.findAll(tag,pageable));
    }

    @GetMapping("/geo")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<List<RestaurantResponseDTO>> findByCoordinates(
        @RequestParam(name = "tag",defaultValue = "") String tag,
        @RequestParam(name = "longitude") String longitude,
        @RequestParam(name = "latitude") String latitude,
        @RequestParam(name = "maxDistance",defaultValue = "") String maxDistance
    ){
        return ResponseEntity.ok(service.findByCoordinates(tag,longitude, latitude, maxDistance));
    }

    @GetMapping("/geo/nearMe")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<List<RestaurantResponseDTO>> findByCoordinatesNearMe(
        @RequestParam(name = "tag",defaultValue = "") String tag,
        @RequestParam(name = "maxDistance",defaultValue = "") String maxDistance
    ){
        return ResponseEntity.ok(service.findByCoordinatesNearMe(tag, maxDistance));
    }

    @GetMapping("/{id}/products")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<Page<ProductResponseDTO>> findProductsByRestaurantId(Pageable pageable, @PathVariable String id){
        return ResponseEntity.ok(service.findAllProductsByRestaurantId(id, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<RestaurantResponseDTO> findById(@PathVariable String id){
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<RestaurantResponseDTO> create(@RequestBody @Valid RestaurantRequestDTO dto){
        RestaurantResponseDTO response = service.create(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
            .buildAndExpand(response.getId()).toUri();
        return ResponseEntity.created(uri).body(response);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<RestaurantResponseDTO> update(@PathVariable String id,@RequestBody @Valid RestaurantRequestDTO dto){
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
