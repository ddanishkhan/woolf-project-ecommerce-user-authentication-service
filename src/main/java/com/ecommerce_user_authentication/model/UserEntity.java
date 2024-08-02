package com.ecommerce_user_authentication.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class UserEntity extends BaseModel {
    private String email;
    private String password;

    @ManyToMany
    private Set<Role> roles = new HashSet<>();  // Move into separate table.
    /**
     * CREATE TABLE roles (
     *     id INT AUTO_INCREMENT PRIMARY KEY,
     *     name VARCHAR(50) UNIQUE NOT NULL
     * );
     *
     * CREATE TABLE user_roles (
     *     user_id INT,
     *     role_id INT,
     *     PRIMARY KEY (user_id, role_id),
     *     FOREIGN KEY (user_id) REFERENCES users(id),
     *     FOREIGN KEY (role_id) REFERENCES roles(id)
     * );
     */
}
