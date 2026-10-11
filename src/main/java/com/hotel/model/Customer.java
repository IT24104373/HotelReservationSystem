package com.hotel.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "customers")
public class Customer extends AbstractUser {
    public Customer() {}

    public Customer(String name, String email, String password) {
        super(name, email, password);
    }

    @Override
    public String getRole() {
        return "CUSTOMER";
    }
}