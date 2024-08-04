package com.ecommerce_user_authentication.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
public class UserEntity extends BaseModel {

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @CreationTimestamp
    private Date createdAt;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<RoleEntity> roleEntities = new HashSet<>();  // Move into separate table.
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
