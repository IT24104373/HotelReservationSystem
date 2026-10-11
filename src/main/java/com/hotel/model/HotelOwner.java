package com.hotel.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "hotel_owners")
public class HotelOwner extends AbstractUser {
    public HotelOwner() {}

    public HotelOwner(String name, String email, String password) {
        super(name, email, password);
    }

    @Override
    public String getRole() {
        return "OWNER";
    }
}