package com.ecommerce_user_authentication.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Table(name = "roles")
@Entity
@Getter
@Setter
public class RoleEntity extends BaseModel{

    @Column(unique = true, nullable = false)
    private String name;

    //CREATE TABLE roles (
//     *     id INT AUTO_INCREMENT PRIMARY KEY,
//     *     name VARCHAR(50) UNIQUE NOT NULL
//     * );
}
