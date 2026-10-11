package com.hotel.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User extends AbstractUser {
    private String role;

    public User() {}

    public User(String name, String email, String password, String role) {
        super(name, email, password);
        this.role = role;
    }

    @Override
    public String getRole() {
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}