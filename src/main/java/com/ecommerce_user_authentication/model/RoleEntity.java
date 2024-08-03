package com.ecommerce_user_authentication.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class RoleEntity extends BaseModel{

    private String name;

    //CREATE TABLE roles (
//     *     id INT AUTO_INCREMENT PRIMARY KEY,
//     *     name VARCHAR(50) UNIQUE NOT NULL
//     * );
}
