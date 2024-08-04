package com.ecommerce_user_authentication.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Getter
@Setter
public class SessionEntity extends BaseModel {

    private String token;

    private Date expiringAt;

    @CreationTimestamp
    private Date loginAt;

    @ManyToOne
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    private SessionStatus sessionStatus;

}