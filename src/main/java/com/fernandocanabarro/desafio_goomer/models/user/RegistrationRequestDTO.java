package com.fernandocanabarro.desafio_goomer.models.user;

import com.fernandocanabarro.desafio_goomer.models.restaurant.embedded.AddressRequestDTO;
import com.fernandocanabarro.desafio_goomer.models.user.validations.RegistrationRequestDTOValid;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@RegistrationRequestDTOValid
public class RegistrationRequestDTO {

    @NotBlank(message = "Campo Requerido")
    private String fullName;
    @Pattern(regexp = "^[A-Za-z0-9+._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",message = "Email deve estar em formato válido")
    private String email;
    @Size(min = 8,message = "Senha deve conter no mínimo 8 caracteres")
    private String password;
    private AddressRequestDTO address;
}
