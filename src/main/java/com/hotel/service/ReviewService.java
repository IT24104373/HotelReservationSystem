package com.hotel.service;

import com.hotel.model.Review;
import com.hotel.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * ============================================================================
 * MEMBER 3 MODULE: Review Service (Full CRUD Implementation)
 * OOP Concept: Encapsulation & Business Logic Separation
 * ============================================================================
 */
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    // 1. CREATE: Add a new review for a hotel
    public Review addReview(Long customerId, String customerName, Long hotelId, String hotelName, int rating, String comment) {
        Review review = new Review(customerId, customerName, hotelId, hotelName, rating, comment);
        return reviewRepository.save(review);
    }

    // 2. READ: Get all reviews for a specific hotel
    public List<Review> getReviewsByHotel(Long hotelId) {
        return reviewRepository.findByHotelId(hotelId);
    }

    // 2. READ: Get all reviews written by a specific customer
    public List<Review> getReviewsByCustomer(Long customerId) {
        return reviewRepository.findByCustomerId(customerId);
    }

    // 2. READ: Get all reviews across all hotels
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    // 3. UPDATE: Edit an existing review (rating & comment)
    public boolean updateReview(Long reviewId, int rating, String comment) {
        Review review = reviewRepository.findById(reviewId).orElse(null);
        if (review != null) {
            review.setRating(rating); // validated by setter
            review.setComment(comment);
            review.setReviewDate(LocalDate.now());
            reviewRepository.save(review);
            return true;
        }
        return false;
    }

    // 4. DELETE: Delete a review
    public boolean deleteReview(Long reviewId) {
        if (reviewRepository.existsById(reviewId)) {
            reviewRepository.deleteById(reviewId);
            return true;
        }
        return false;
    }
}
