package com.ecommerce_user_authentication.repository;

import com.ecommerce_user_authentication.model.RoleEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface RoleRepository extends CrudRepository<RoleEntity, Long> {


    Set<RoleEntity> findAllByIdIn(List<Long> roleIds);

}
