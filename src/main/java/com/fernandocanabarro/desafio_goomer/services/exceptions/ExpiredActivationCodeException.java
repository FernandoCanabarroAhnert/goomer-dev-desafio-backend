package com.fernandocanabarro.desafio_goomer.services.exceptions;

public class ExpiredActivationCodeException extends RuntimeException{

    public ExpiredActivationCodeException(String email){
        super("O Código de Ativação expirou. Um novo e-mail de confirmação será enviado para " + email);
    }
}
