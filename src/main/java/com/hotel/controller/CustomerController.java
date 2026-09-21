package com.hotel.controller;

import com.hotel.model.User;
import com.hotel.service.HotelService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    private final HotelService hotelService;

    public CustomerController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"CUSTOMER".equals(user.getRole())) return "redirect:/";

        model.addAttribute("user", user);
        model.addAttribute("hotels", hotelService.getHotels());
        model.addAttribute("bookings", hotelService.getCustomerBookings(user.getId()));
        return "customer_dashboard";
    }

    @PostMapping("/book")
    public String bookHotel(@RequestParam Long hotelId, @RequestParam String checkInDate,
                            @RequestParam String checkOutDate, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            boolean success = hotelService.createBooking(user.getId(), hotelId, checkInDate, checkOutDate);
            if (!success) {
                return "redirect:/customer/dashboard?error=unavailable";
            }
        }
        return "redirect:/customer/dashboard";
    }

    @PostMapping("/update-booking")
    public String updateBooking(@RequestParam Long bookingId, @RequestParam String checkInDate, @RequestParam String checkOutDate) {
        boolean success = hotelService.updateBookingDate(bookingId, checkInDate, checkOutDate);
        if (!success) {
            return "redirect:/customer/dashboard?error=unavailable";
        }
        return "redirect:/customer/dashboard";
    }

    @PostMapping("/cancel-booking")
    public String cancelBooking(@RequestParam Long bookingId) {
        hotelService.cancelBooking(bookingId);
        return "redirect:/customer/dashboard";
    }
}