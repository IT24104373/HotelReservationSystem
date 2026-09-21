package com.hotel.model;

import jakarta.persistence.Entity;

@Entity
public class HotelOwner extends User {
    public HotelOwner() {
        setRole("OWNER");
    }

    public HotelOwner(String name, String email, String password) {
        super(name, email, password, "OWNER");
    }
}