package com.hotel.controller;

import com.hotel.model.Hotel;
import com.hotel.model.Review;
import com.hotel.model.User;
import com.hotel.service.HotelService;
import com.hotel.service.ReviewService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles owner dashboard and hotel property requests.
 */
@Controller
@RequestMapping("/owner")
public class OwnerController {

    private final HotelService hotelService;
    private final ReviewService reviewService;
    private final Path uploadDirectory = Paths.get(System.getProperty("user.dir"), "uploads", "hotels");

    public OwnerController(HotelService hotelService, ReviewService reviewService) {
        this.hotelService = hotelService;
        this.reviewService = reviewService;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"OWNER".equals(user.getRole())) {
            return "redirect:/login";
        }

        var hotels = hotelService.getHotels(user.getId());
        Map<Long, Object> hotelBookings = new HashMap<>();
        Map<Long, List<Review>> hotelReviews = new HashMap<>();

        for (Hotel hotel : hotels) {
            hotelBookings.put(hotel.getId(), hotelService.getBookingsForHotel(hotel.getId()));
            hotelReviews.put(hotel.getId(), reviewService.getReviewsByHotel(hotel.getId()));
        }

        model.addAttribute("user", user);
        model.addAttribute("hotels", hotels);
        model.addAttribute("hotelBookings", hotelBookings);
        model.addAttribute("hotelReviews", hotelReviews);
        return "owner_dashboard";
    }

    @PostMapping("/add-hotel")
    public String addHotel(@RequestParam String name,
                           @RequestParam String location,
                           @RequestParam long pricePerNight,
                           @RequestParam(required = false, defaultValue = "") String imageUrl,
                           @RequestParam(required = false) MultipartFile imageFile,
                           @RequestParam String description,
                           HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            imageUrl = storeImage(imageFile, imageUrl);
            if (imageUrl == null) {
                return "redirect:/owner/dashboard?error=invalid_image";
            }
            boolean success = hotelService.saveHotel(new Hotel(name, location, pricePerNight, user.getId(), imageUrl, description));
            if (!success) {
                return "redirect:/owner/dashboard?error=invalid_price";
            }
        }
        return "redirect:/owner/dashboard";
    }

    @PostMapping("/update-hotel")
    public String updateHotel(@RequestParam Long hotelId,
                              @RequestParam String name,
                              @RequestParam String location,
                              @RequestParam long pricePerNight,
                              @RequestParam(required = false, defaultValue = "") String imageUrl,
                              @RequestParam(required = false) MultipartFile imageFile,
                              @RequestParam String description) {
        imageUrl = storeImage(imageFile, imageUrl);
        if (imageUrl == null) {
            return "redirect:/owner/dashboard?error=invalid_image";
        }
        boolean success = hotelService.updateHotel(hotelId, name, location, pricePerNight, imageUrl, description);
        if (!success) {
            return "redirect:/owner/dashboard?error=invalid_price";
        }
        return "redirect:/owner/dashboard";
    }

    @PostMapping("/delete-hotel")
    public String deleteHotel(@RequestParam Long hotelId) {
        hotelService.deleteHotel(hotelId);
        return "redirect:/owner/dashboard";
    }

    private String storeImage(MultipartFile imageFile, String imageUrl) {
        if (imageFile == null || imageFile.isEmpty()) {
            return imageUrl == null ? "" : imageUrl.trim();
        }
        if (imageFile.getContentType() == null || !imageFile.getContentType().startsWith("image/")) {
            return null;
        }

        String contentType = imageFile.getContentType();
        String extension = contentType.substring(contentType.indexOf('/') + 1).replace("jpeg", "jpg");
        Path target = uploadDirectory.resolve(UUID.randomUUID() + "." + extension);
        try {
            Files.createDirectories(uploadDirectory);
            Files.copy(imageFile.getInputStream(), target);
            return "/uploads/hotels/" + target.getFileName();
        } catch (IOException exception) {
            return null;
        }
    }
}