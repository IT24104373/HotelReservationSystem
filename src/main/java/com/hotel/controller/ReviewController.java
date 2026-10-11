package com.hotel.controller;

import com.hotel.model.User;
import com.hotel.service.ReviewService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * ============================================================================
 * MEMBER 3 MODULE: Review Controller
 * Handles CRUD operations for Customer Reviews & Ratings
 * ============================================================================
 */
@Controller
@RequestMapping("/review")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // 1. CREATE: Add a new review
    @PostMapping("/add")
    public String addReview(@RequestParam Long hotelId,
                            @RequestParam String hotelName,
                            @RequestParam int rating,
                            @RequestParam String comment,
                            HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null && "CUSTOMER".equals(user.getRole())) {
            reviewService.addReview(user.getId(), user.getName(), hotelId, hotelName, rating, comment);
        }
        return "redirect:/customer/dashboard#my-reviews";
    }

    // 2. UPDATE: Edit an existing review
    @PostMapping("/update")
    public String updateReview(@RequestParam Long reviewId,
                               @RequestParam int rating,
                               @RequestParam String comment,
                               HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null && "CUSTOMER".equals(user.getRole())) {
            reviewService.updateReview(reviewId, rating, comment);
        }
        return "redirect:/customer/dashboard#my-reviews";
    }

    // 3. DELETE: Delete an existing review
    @PostMapping("/delete")
    public String deleteReview(@RequestParam Long reviewId,
                               HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null && "CUSTOMER".equals(user.getRole())) {
            reviewService.deleteReview(reviewId);
        }
        return "redirect:/customer/dashboard#my-reviews";
    }
}
