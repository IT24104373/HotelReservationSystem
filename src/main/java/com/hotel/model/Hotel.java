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
    private long pricePerNight;
    private Long ownerId;

    @Column(length = 1000)
    private String imageUrl;

    @Column(length = 2000)
    private String description;

    public Hotel() {}

    public Hotel(String name, String location, long pricePerNight, Long ownerId, String imageUrl, String description) {
        this.name = name;
        this.location = location;
        this.pricePerNight = pricePerNight;
        this.ownerId = ownerId;
        this.imageUrl = imageUrl;
        this.description = description;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public long getPricePerNight() { return pricePerNight; }
    public void setPricePerNight(long pricePerNight) {
        if (pricePerNight < 1000) {
            this.pricePerNight = 1000;
        } else {
            this.pricePerNight = pricePerNight;
        }
    }

    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}