package com.hotel.model;

import jakarta.persistence.*;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerId;
    private Long hotelId;
    private String hotelName;
    private String checkInDate;
    private String checkOutDate;
    private String status; // BOOKED, CANCELLED

    public Booking() {}

    public Booking(Long customerId, Long hotelId, String hotelName, String checkInDate, String checkOutDate, String status) {
        this.customerId = customerId;
        this.hotelId = hotelId;
        this.hotelName = hotelName;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public Long getHotelId() { return hotelId; }
    public void setHotelId(Long hotelId) { this.hotelId = hotelId; }

    public String getHotelName() { return hotelName; }
    public void setHotelName(String hotelName) { this.hotelName = hotelName; }

    public String getCheckInDate() { return checkInDate; }
    public void setCheckInDate(String checkInDate) { this.checkInDate = checkInDate; }

    public String getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(String checkOutDate) { this.checkOutDate = checkOutDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}