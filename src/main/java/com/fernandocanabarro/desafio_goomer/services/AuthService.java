package com.fernandocanabarro.desafio_goomer.services;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fernandocanabarro.desafio_goomer.models.restaurant.embedded.Address;
import com.fernandocanabarro.desafio_goomer.models.role.RoleRepository;
import com.fernandocanabarro.desafio_goomer.models.user.RegistrationRequestDTO;
import com.fernandocanabarro.desafio_goomer.models.user.User;
import com.fernandocanabarro.desafio_goomer.models.user.UserDTO;
import com.fernandocanabarro.desafio_goomer.models.user.UserRepository;
import com.fernandocanabarro.desafio_goomer.utils.CustomUserUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AddressService addressService;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserUtils customUserUtils;

    @Transactional(readOnly = true)
    public User getConnectedUser(){
        try{
            String email = customUserUtils.getLoggedUserName();
            return userRepository.findByEmail(email).get();
        }
        catch (Exception e){
            throw new UsernameNotFoundException("User not found");
        }
    }

    @Transactional
    public UserDTO register(RegistrationRequestDTO dto){
        User user = new User();
        toEntity(user,dto);
        user = userRepository.save(user);
        return new UserDTO(user);
    }

    private final void toEntity(User user, RegistrationRequestDTO dto) {
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        Address address = addressService.getAddressFromRequest(dto.getAddress());
        user.setAddress(address);
        user.setGeoPoint(addressService.convertAddressToPoint(address));
        user.addRole(roleRepository.findByAuthority("ROLE_USER"));
    }

}
