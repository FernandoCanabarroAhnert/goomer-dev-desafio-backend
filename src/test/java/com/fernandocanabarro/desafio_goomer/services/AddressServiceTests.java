package com.fernandocanabarro.desafio_goomer.services;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException.BadRequest;

import com.fernandocanabarro.desafio_goomer.factories.AddressFactory;
import com.fernandocanabarro.desafio_goomer.models.restaurant.embedded.Address;
import com.fernandocanabarro.desafio_goomer.models.restaurant.embedded.AddressRequestDTO;
import com.fernandocanabarro.desafio_goomer.models.restaurant.embedded.NominatimResponse;
import com.fernandocanabarro.desafio_goomer.models.restaurant.embedded.ViaCepResponse;
import com.fernandocanabarro.desafio_goomer.services.exceptions.ResourceNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
public class AddressServiceTests {

    @InjectMocks
    private AddressService addressService;
    @Mock
    private RestTemplate restTemplate;

    private AddressRequestDTO addressRequestDTO;
    private ViaCepResponse viaCepResponse;
    private NominatimResponse nominatimResponse;
    private Address address;

    @BeforeEach
    public void setup() throws Exception{
        addressRequestDTO = new AddressRequestDTO("cep", "numero", "complemento");
        viaCepResponse = new ViaCepResponse("cep", "logradouro", "bairro", "cidade", "estado");
        nominatimResponse = new NominatimResponse("1", "1");
        address = AddressFactory.getAddress();
    }

    @Test
    public void getAddressFromRequestShouldReturnAddressWhenCEPExists(){
        ResponseEntity<ViaCepResponse> responseEntity = new ResponseEntity<ViaCepResponse>(viaCepResponse, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(ViaCepResponse.class))).thenReturn(responseEntity);

        Address response = addressService.getAddressFromRequest(addressRequestDTO);

        assertThat(response).isNotNull();
        assertThat(response.getBairro()).isEqualTo("bairro");
        assertThat(response.getLogradouro()).isEqualTo("logradouro");
        assertThat(response.getNumero()).isEqualTo("numero");
        assertThat(response.getCep()).isEqualTo("cep");
    }

    @Test
    public void getAddressFromRequestShouldThrowResourceNotFoundExceptionWhenWhenCEPDoesNotExist(){
        when(restTemplate.getForEntity(anyString(), eq(ViaCepResponse.class))).thenThrow(BadRequest.class);

        assertThatThrownBy(() -> addressService.getAddressFromRequest(addressRequestDTO)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void convertAddressToPointShouldReturnGeoJsonPointWhenAddressIsValid(){
        NominatimResponse[] nominatimArray = new NominatimResponse[1];
        nominatimArray[0] = nominatimResponse;

        ResponseEntity<NominatimResponse[]> responseEntity = new ResponseEntity<NominatimResponse[]>(nominatimArray, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(NominatimResponse[].class))).thenReturn(responseEntity);

        GeoJsonPoint response = addressService.convertAddressToPoint(address);

        assertThat(response).isNotNull();
        assertThat(response.getX()).isEqualTo(1.0);
        assertThat(response.getY()).isEqualTo(1.0);
    }

    @Test
    public void convertAddressToPointShouldThrowResourceNotFoundExceptionWhenNominatimResponseIsEmpty(){
        NominatimResponse[] nominatimArray = new NominatimResponse[0];

        ResponseEntity<NominatimResponse[]> responseEntity = new ResponseEntity<NominatimResponse[]>(nominatimArray, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(NominatimResponse[].class))).thenReturn(responseEntity);

        assertThatThrownBy(() -> addressService.convertAddressToPoint(address)).isInstanceOf(ResourceNotFoundException.class);
    }
}
