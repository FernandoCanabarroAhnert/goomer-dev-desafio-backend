package com.fernandocanabarro.desafio_goomer.factories;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import com.fernandocanabarro.desafio_goomer.models.user.User;

import java.util.Set;

public class UserFactory {

    public static User getUser(){
        return new User("1", "name", "email", "12345", 
            AddressFactory.getAddress(), new GeoJsonPoint(1.0,1.0), Set.of(RoleFactory.getRole()));
    }
}
