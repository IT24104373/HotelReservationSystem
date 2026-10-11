package com.hotel.model;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * ============================================================================
 * MEMBER 3 MODULE: Review & Rating Management
 * OOP Concept: Encapsulation (Private fields with getter/setter validations)
 * ============================================================================
 */
@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerId;
    private String customerName;
    private Long hotelId;
    private String hotelName;

    // Encapsulation validation: rating must be between 1 and 5
    private int rating;

    @Column(length = 1000)
    private String comment;

    private LocalDate reviewDate;

    public Review() {
        this.reviewDate = LocalDate.now();
    }

    public Review(Long customerId, String customerName, Long hotelId, String hotelName, int rating, String comment) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.hotelId = hotelId;
        this.hotelName = hotelName;
        setRating(rating); // using setter for validation
        this.comment = comment;
        this.reviewDate = LocalDate.now();
    }

    // Getters and Setters with Encapsulation Validation
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Long getHotelId() {
        return hotelId;
    }

    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public int getRating() {
        return rating;
    }

    // Encapsulation validation rule
    public void setRating(int rating) {
        if (rating < 1) {
            this.rating = 1;
        } else if (rating > 5) {
            this.rating = 5;
        } else {
            this.rating = rating;
        }
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDate getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(LocalDate reviewDate) {
        this.reviewDate = reviewDate;
    }
}
