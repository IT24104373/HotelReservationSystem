package com.hotel.model;

import jakarta.persistence.*;

@Entity
@Table(name = "hotels")
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String location;
    private long pricePerNight; // Whole number only (Minimum LKR 1000)
    private Long ownerId;

    public Hotel() {}

    public Hotel(String name, String location, long pricePerNight, Long ownerId) {
        this.name = name;
        this.location = location;
        this.pricePerNight = pricePerNight;
        this.ownerId = ownerId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public long getPricePerNight() { return pricePerNight; }
    public void setPricePerNight(long pricePerNight) { this.pricePerNight = pricePerNight; }

    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
}