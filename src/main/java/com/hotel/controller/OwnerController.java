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

@Controller
@RequestMapping("/owner")
public class OwnerController {

    private final HotelService hotelService;

    public OwnerController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"OWNER".equals(user.getRole())) return "redirect:/";

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

    @PostMapping("/add-hotel")
    public String addHotel(@RequestParam String name, @RequestParam String location,
                           @RequestParam long pricePerNight, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            boolean success = hotelService.saveHotel(new Hotel(name, location, pricePerNight, user.getId()));
            if (!success) {
                return "redirect:/owner/dashboard?error=invalid_price";
            }
        }
        return "redirect:/owner/dashboard";
    }

    @PostMapping("/delete-hotel")
    public String deleteHotel(@RequestParam Long hotelId) {
        hotelService.deleteHotel(hotelId);
        return "redirect:/owner/dashboard";
    }
}