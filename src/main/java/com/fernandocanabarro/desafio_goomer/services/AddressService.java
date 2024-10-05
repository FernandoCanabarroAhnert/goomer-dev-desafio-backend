package com.fernandocanabarro.desafio_goomer.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException.BadRequest;

import com.fernandocanabarro.desafio_goomer.models.restaurant.embedded.Address;
import com.fernandocanabarro.desafio_goomer.models.restaurant.embedded.AddressRequestDTO;
import com.fernandocanabarro.desafio_goomer.models.restaurant.embedded.NominatimResponse;
import com.fernandocanabarro.desafio_goomer.models.restaurant.embedded.ViaCepResponse;
import com.fernandocanabarro.desafio_goomer.services.exceptions.ResourceNotFoundException;

import java.util.List;
import java.util.Arrays;

@Service
public class AddressService {

    @Autowired
    private RestTemplate restTemplate;

    private String viaCepBaseUrl = "https://viacep.com.br/ws/";
    private String nominatimBaseUrl = "https://nominatim.openstreetmap.org/search?q=";

    public Address getAddressFromRequest(AddressRequestDTO dto){
        try{
            String url = viaCepBaseUrl + dto.getCep() + "/json/";
            ResponseEntity<ViaCepResponse> response = restTemplate.getForEntity(url, ViaCepResponse.class);
            ViaCepResponse body = response.getBody();
            Address address = convertToAddress(body);
            address.setNumero(dto.getNumero());
            address.setComplemento(dto.getComplemento());
            return address;
        }
        catch (BadRequest e){
            throw new ResourceNotFoundException("CEP não encontrado");
        }
    }

    private Address convertToAddress(ViaCepResponse body) {
        Address address = new Address();
        address.setLogradouro(body.getLogradouro());
        address.setCep(body.getCep());
        address.setBairro(body.getBairro());
        address.setCidade(body.getLocalidade());
        address.setEstado(body.getUf());
        return address;
    }

    public GeoJsonPoint convertAddressToPoint(Address address){
        String url = nominatimBaseUrl + address.getCep() + "," + address.getLogradouro() + "," + address.getNumero() + "&format=json";
        ResponseEntity<NominatimResponse[]> response = restTemplate.getForEntity(url, NominatimResponse[].class);
        List<NominatimResponse> places = Arrays.asList(response.getBody());
        if (places.isEmpty()) {
            throw new ResourceNotFoundException("Endereço não encontrado");
        }
        NominatimResponse body = places.get(0);
        GeoJsonPoint point = new GeoJsonPoint(Double.parseDouble(body.getLon()), Double.parseDouble(body.getLat()));
        return point;
    }
}
