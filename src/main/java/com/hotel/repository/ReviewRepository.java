package com.hotel.repository;

import com.hotel.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ============================================================================
 * MEMBER 3 MODULE: Review Repository Interface
 * OOP Concept: Abstraction (Spring Data JPA Interface Contract)
 * ============================================================================
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Find all reviews written for a specific hotel
    List<Review> findByHotelId(Long hotelId);

    // Find all reviews written by a specific customer
    List<Review> findByCustomerId(Long customerId);
}
