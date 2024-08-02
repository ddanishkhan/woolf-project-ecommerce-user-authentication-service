package com.ecommerce_user_authentication.model;

import jakarta.persistence.Entity;

@Entity
public class Role extends BaseModel{

    private String name;

    //CREATE TABLE roles (
//     *     id INT AUTO_INCREMENT PRIMARY KEY,
//     *     name VARCHAR(50) UNIQUE NOT NULL
//     * );
}
