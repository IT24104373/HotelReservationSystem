package com.hotel.service;

import com.hotel.model.Review;
import com.hotel.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Provides review persistence and validation operations.
 */
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public Review addReview(Long customerId, String customerName, Long hotelId, String hotelName, int rating, String comment) {
        Review review = new Review(customerId, customerName, hotelId, hotelName, rating, comment);
        return reviewRepository.save(review);
    }

    public List<Review> getReviewsByHotel(Long hotelId) {
        return reviewRepository.findByHotelId(hotelId);
    }

    public List<Review> getReviewsByCustomer(Long customerId) {
        return reviewRepository.findByCustomerId(customerId);
    }

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    public boolean updateReview(Long reviewId, int rating, String comment) {
        Review review = reviewRepository.findById(reviewId).orElse(null);
        if (review != null) {
            review.setRating(rating);
            review.setComment(comment);
            review.setReviewDate(LocalDate.now());
            reviewRepository.save(review);
            return true;
        }
        return false;
    }

    public boolean deleteReview(Long reviewId) {
        if (reviewRepository.existsById(reviewId)) {
            reviewRepository.deleteById(reviewId);
            return true;
        }
        return false;
    }
}
