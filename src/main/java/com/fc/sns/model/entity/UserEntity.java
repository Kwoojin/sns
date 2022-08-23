package com.fc.sns.model.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Setter
@Getter
@Entity
public class UserEntity {

    @Id
    private Long id;

    @Column(name = "user_name")
    private String userName;

    private String password;
}
