package com.hotel.controller;

import com.hotel.model.Hotel;
import com.hotel.model.User;
import com.hotel.service.HotelService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * ============================================================================
 * MEMBER 1 MODULE: Hotel Owner Management Controller
 * Handles full CRUD operations for Hotel Properties
 * ============================================================================
 */
@Controller
@RequestMapping("/owner")
public class OwnerController {

    private final HotelService hotelService;

    public OwnerController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    // 1. READ: Owner Dashboard listing all hotels owned by this user
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"OWNER".equals(user.getRole())) {
            return "redirect:/login";
        }

        var hotels = hotelService.getHotels(user.getId());
        Map<Long, Object> hotelBookings = new HashMap<>();

        for (Hotel hotel : hotels) {
            hotelBookings.put(hotel.getId(), hotelService.getBookingsForHotel(hotel.getId()));
        }

        model.addAttribute("user", user);
        model.addAttribute("hotels", hotels);
        model.addAttribute("hotelBookings", hotelBookings);
        return "owner_dashboard";
    }

    // 2. CREATE: Add a new hotel property
    @PostMapping("/add-hotel")
    public String addHotel(@RequestParam String name,
                           @RequestParam String location,
                           @RequestParam long pricePerNight,
                           @RequestParam String imageUrl,
                           @RequestParam String description,
                           HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            boolean success = hotelService.saveHotel(new Hotel(name, location, pricePerNight, user.getId(), imageUrl, description));
            if (!success) {
                return "redirect:/owner/dashboard?error=invalid_price";
            }
        }
        return "redirect:/owner/dashboard";
    }

    // 3. UPDATE: Edit an existing hotel property
    @PostMapping("/update-hotel")
    public String updateHotel(@RequestParam Long hotelId,
                              @RequestParam String name,
                              @RequestParam String location,
                              @RequestParam long pricePerNight,
                              @RequestParam String imageUrl,
                              @RequestParam String description) {
        boolean success = hotelService.updateHotel(hotelId, name, location, pricePerNight, imageUrl, description);
        if (!success) {
            return "redirect:/owner/dashboard?error=invalid_price";
        }
        return "redirect:/owner/dashboard";
    }

    // 4. DELETE: Delete a hotel property
    @PostMapping("/delete-hotel")
    public String deleteHotel(@RequestParam Long hotelId) {
        hotelService.deleteHotel(hotelId);
        return "redirect:/owner/dashboard";
    }
}