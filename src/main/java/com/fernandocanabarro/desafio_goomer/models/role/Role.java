package com.fernandocanabarro.desafio_goomer.models.role;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "roles")
public class Role implements GrantedAuthority{
    
    @Id
    private String id;
    private String authority;

    @Override
    public String getAuthority(){
        return authority;
    }
}
