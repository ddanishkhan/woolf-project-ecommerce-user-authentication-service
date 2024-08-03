package com.ecommerce_user_authentication.repository;

import com.ecommerce_user_authentication.model.SessionEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SessionRepository extends CrudRepository<SessionEntity, Long> {

    Optional<SessionEntity> findByTokenAndEmail(String token, String email);

}
