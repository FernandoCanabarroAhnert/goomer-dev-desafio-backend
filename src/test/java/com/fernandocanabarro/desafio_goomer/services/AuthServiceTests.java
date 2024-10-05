package com.fernandocanabarro.desafio_goomer.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fernandocanabarro.desafio_goomer.factories.AddressFactory;
import com.fernandocanabarro.desafio_goomer.factories.UserFactory;
import com.fernandocanabarro.desafio_goomer.models.restaurant.embedded.Address;
import com.fernandocanabarro.desafio_goomer.models.restaurant.embedded.AddressRequestDTO;
import com.fernandocanabarro.desafio_goomer.models.role.RoleRepository;
import com.fernandocanabarro.desafio_goomer.models.user.RegistrationRequestDTO;
import com.fernandocanabarro.desafio_goomer.models.user.User;
import com.fernandocanabarro.desafio_goomer.models.user.UserDTO;
import com.fernandocanabarro.desafio_goomer.models.user.UserRepository;
import com.fernandocanabarro.desafio_goomer.utils.CustomUserUtils;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTests {

    @InjectMocks
    private AuthService authService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AddressService addressService;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private CustomUserUtils customUserUtils;

    private RegistrationRequestDTO registrationRequestDTO;
    private User user;
    private Address address;

    @BeforeEach
    public void setup() throws Exception{
        registrationRequestDTO = new RegistrationRequestDTO("name", "email", 
            "12345", new AddressRequestDTO("cep", "numero", "complemento"));
        user = UserFactory.getUser();
        address = AddressFactory.getAddress();
    }

    @Test
    public void getConnectedUserShouldReturnUserShouldReturnUser(){
        when(customUserUtils.getLoggedUserName()).thenReturn("email");
        when(userRepository.findByEmail("email")).thenReturn(Optional.of(user));

        User response = authService.getConnectedUser();

        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo("email");
        assertThat(response.getFullName()).isEqualTo("name");
        assertThat(response.getPassword()).isEqualTo("12345");
    }

    @Test
    public void getConnectedUserShouldThrowUsernameNotFoundExceptionWhenNoUserIsConnected(){
        doThrow(ClassCastException.class).when(customUserUtils).getLoggedUserName();

        assertThatThrownBy(() -> authService.getConnectedUser()).isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    public void registerShouldReturnUserDTO(){
        GeoJsonPoint point = new GeoJsonPoint(1.0,1.0);
        when(passwordEncoder.encode(anyString())).thenReturn("12345");
        when(addressService.getAddressFromRequest(any(AddressRequestDTO.class))).thenReturn(address);
        when(addressService.convertAddressToPoint(any(Address.class))).thenReturn(point);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDTO response = authService.register(registrationRequestDTO);

        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo("email");
        assertThat(response.getFullName()).isEqualTo("name");
        assertThat(response.getRoles().get(0).getAuthority()).isEqualTo("authority");
    }

}
