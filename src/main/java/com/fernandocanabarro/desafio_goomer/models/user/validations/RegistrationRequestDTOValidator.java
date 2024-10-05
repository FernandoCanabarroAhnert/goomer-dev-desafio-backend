package com.fernandocanabarro.desafio_goomer.models.user.validations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;

import com.fernandocanabarro.desafio_goomer.models.exceptions.FieldMessage;
import com.fernandocanabarro.desafio_goomer.models.user.RegistrationRequestDTO;
import com.fernandocanabarro.desafio_goomer.models.user.User;
import com.fernandocanabarro.desafio_goomer.models.user.UserRepository;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RegistrationRequestDTOValidator implements ConstraintValidator<RegistrationRequestDTOValid,RegistrationRequestDTO>{

    @Autowired
    private UserRepository userRepository;

    @Override
    public void initialize(RegistrationRequestDTOValid ann){}

    @Override
    public boolean isValid(RegistrationRequestDTO request, ConstraintValidatorContext context) {

        List<FieldMessage> errors = new ArrayList<>();

        Optional<User> user = userRepository.findByEmail(request.getEmail());
        if (user.isPresent()) {
            errors.add(new FieldMessage("email", "Este e-mail já existe"));
        }

        String password = request.getPassword();

        if (!Pattern.matches(".*[A-Z].*", password)) {
            errors.add(new FieldMessage("password", "A Senha deve possuir pelo menos 1 letra maiúscula"));
        }
        if (!Pattern.matches(".*[a-z].*", password)) {
            errors.add(new FieldMessage("password", "A Senha deve possuir pelo menos 1 letra minúscula"));
        }
        if (!Pattern.matches(".*[0-9].*", password)) {
            errors.add(new FieldMessage("password", "A Senha deve possuir pelo menos 1 número"));
        }
        if (!Pattern.matches(".*[\\W].*", password)) {
            errors.add(new FieldMessage("password", "A Senha deve possuir pelo menos 1 caractere especial"));
        }

        for (FieldMessage f : errors){
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(f.getMessage())
                .addPropertyNode(f.getFieldName())
                .addConstraintViolation();
        }

        return errors.isEmpty();
    }

}
