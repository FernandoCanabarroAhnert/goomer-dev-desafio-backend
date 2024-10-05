package com.fernandocanabarro.desafio_goomer.models.user;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fernandocanabarro.desafio_goomer.models.restaurant.embedded.Address;
import com.fernandocanabarro.desafio_goomer.models.role.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private String id;
    private String fullName;
    private String email;
    private String password;
    private Address address;
    private GeoJsonPoint geoPoint;
    private Set<Role> roles = new HashSet<>();

}
