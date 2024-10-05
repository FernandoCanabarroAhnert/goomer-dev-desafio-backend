package com.fernandocanabarro.desafio_goomer.models.user;

import java.util.ArrayList;
import java.util.List;

import com.fernandocanabarro.desafio_goomer.models.restaurant.embedded.Address;
import com.fernandocanabarro.desafio_goomer.models.role.RoleDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private String id;
    private String fullName;
    private String email;
    private Address address;
    private List<RoleDTO> roles = new ArrayList<>();

    public UserDTO(User entity){
        id = entity.getId();
        fullName = entity.getFullName();
        email = entity.getEmail();
        address = entity.getAddress();
        roles = entity.getRoles().stream().map(RoleDTO::new).toList();
    }
}
