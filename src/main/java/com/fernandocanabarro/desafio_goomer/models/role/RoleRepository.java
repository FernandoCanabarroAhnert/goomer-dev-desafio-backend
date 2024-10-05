package com.fernandocanabarro.desafio_goomer.models.role;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends MongoRepository<Role,String>{

    Role findByAuthority(String authority);
}
