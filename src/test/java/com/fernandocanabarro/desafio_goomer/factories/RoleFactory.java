package com.fernandocanabarro.desafio_goomer.factories;

import com.fernandocanabarro.desafio_goomer.models.role.Role;

public class RoleFactory {

    public static Role getRole(){
        return new Role("1", "authority");
    }
}
