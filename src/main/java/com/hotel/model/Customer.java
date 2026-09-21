package com.hotel.model;

import jakarta.persistence.Entity;

@Entity
public class Customer extends User {
    public Customer() {
        setRole("CUSTOMER");
    }

    public Customer(String name, String email, String password) {
        super(name, email, password, "CUSTOMER");
    }
}