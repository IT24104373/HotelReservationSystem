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
 * Handles customer dashboard and reservation requests.
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

    @PostMapping("/book")
    public String bookHotel(@RequestParam Long hotelId,
                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
                            @RequestParam int guestCount,
                            HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"CUSTOMER".equals(user.getRole())) {
            return "redirect:/login";
        }

        boolean success = hotelService.createBooking(user.getId(), hotelId, checkInDate, checkOutDate, guestCount);
        if (!success) {
            return "redirect:/customer/dashboard?error=invalid";
        }
        return "redirect:/customer/dashboard?success=created";
    }

    @PostMapping("/update-booking")
    public String updateBooking(@RequestParam Long bookingId,
                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
                                @RequestParam int guestCount,
                                HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"CUSTOMER".equals(user.getRole())) {
            return "redirect:/login";
        }

        boolean success = hotelService.updateBooking(user.getId(), bookingId, checkInDate,
                checkOutDate, guestCount);
        if (!success) {
            return "redirect:/customer/dashboard?error=unavailable";
        }
        return "redirect:/customer/dashboard?success=updated";
    }

    @PostMapping("/cancel-booking")
    public String cancelBooking(@RequestParam Long bookingId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"CUSTOMER".equals(user.getRole())) {
            return "redirect:/login";
        }

        if (!hotelService.cancelBooking(user.getId(), bookingId)) {
            return "redirect:/customer/dashboard?error=unavailable";
        }
        return "redirect:/customer/dashboard?success=cancelled";
    }
}