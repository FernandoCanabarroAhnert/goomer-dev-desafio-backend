package com.fernandocanabarro.desafio_goomer.factories;

import com.fernandocanabarro.desafio_goomer.models.restaurant.embedded.Address;

public class AddressFactory {

    public static Address getAddress(){
        return new Address("logradouro", "numero", "complemento", "bairro", "cep", "cidade", "estado");
    }
}
