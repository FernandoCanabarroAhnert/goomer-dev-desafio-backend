package com.fernandocanabarro.desafio_goomer.models.user;

import java.security.Principal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

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
public class User implements UserDetails,Principal{

    @Id
    private String id;
    private String fullName;
    private String email;
    private String password;
    private Address address;
    private GeoJsonPoint geoPoint;
    private Set<Role> roles = new HashSet<>();

    @Override
    public String getName() {
        return this.email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    public boolean hasRole(String roleName){
        for (Role role : roles){
            if (role.getAuthority().equals(roleName)) {
                return true;
            }
        }
        return false;
    }

    public void addRole(Role role){
        this.roles.add(role);
    }
}
