package com.hotel.controller;

import com.hotel.model.User;
import com.hotel.service.HotelService;
import com.hotel.service.ReviewService;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * ============================================================================
 * MEMBER 2 MODULE: Customer & Booking Management Controller
 * Handles full CRUD operations for Hotel Reservations
 * ============================================================================
 */
@Controller
@RequestMapping("/customer")
public class CustomerController {

    private final HotelService hotelService;
    private final ReviewService reviewService;

    public CustomerController(HotelService hotelService, ReviewService reviewService) {
        this.hotelService = hotelService;
        this.reviewService = reviewService;
    }

    // 1. READ: Customer Dashboard displaying hotels, user's bookings, and user's reviews
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"CUSTOMER".equals(user.getRole())) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        model.addAttribute("hotels", hotelService.getHotels());
        model.addAttribute("bookings", hotelService.getCustomerBookings(user.getId()));
        model.addAttribute("reviews", reviewService.getReviewsByCustomer(user.getId()));
        return "customer_dashboard";
    }

    // 2. CREATE: Book a hotel property
    @PostMapping("/book")
    public String bookHotel(@RequestParam Long hotelId,
                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
                            HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            boolean success = hotelService.createBooking(user.getId(), hotelId, checkInDate, checkOutDate);
            if (!success) {
                return "redirect:/customer/dashboard?error=overlap";
            }
        }
        return "redirect:/customer/dashboard";
    }

    // 3. UPDATE: Reschedule / Update booking dates
    @PostMapping("/update-booking")
    public String updateBooking(@RequestParam Long bookingId,
                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate) {
        boolean success = hotelService.updateBooking(bookingId, checkInDate, checkOutDate);
        if (!success) {
            return "redirect:/customer/dashboard?error=overlap";
        }
        return "redirect:/customer/dashboard";
    }

    // 4. DELETE / CANCEL: Cancel a reservation
    @PostMapping("/cancel-booking")
    public String cancelBooking(@RequestParam Long bookingId) {
        hotelService.cancelBooking(bookingId);
        return "redirect:/customer/dashboard";
    }
}