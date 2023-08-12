package com.wanted.internship.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Table(name = "USERS")
@Entity
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String password;

    @Enumerated(value = EnumType.STRING)
    private Authority userRole;

    protected User() {
    }

    private User(String email, String password) {
        this.email = email;
        this.password = password;
        this.userRole = Authority.ROLE_MEMBER;
    }

    public static User of(String email, String encodedPassword) {
        return new User(email, encodedPassword);
    }
}
