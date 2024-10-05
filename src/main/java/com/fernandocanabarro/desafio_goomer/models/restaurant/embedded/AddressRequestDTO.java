package com.fernandocanabarro.desafio_goomer.models.restaurant.embedded;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddressRequestDTO {

    @NotBlank(message = "Campo Requerido")
    @Pattern(regexp = "^(([0-9]{2}\\\\.[0-9]{3}-[0-9]{3})|([0-9]{2}[0-9]{3}-[0-9]{3})|([0-9]{8}))$",
    message = "CEP deve estar em formato válido")
    private String cep;
    @NotBlank(message = "Campo Requerido")
    private String numero;
    private String complemento;
}
