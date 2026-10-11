package com.hotel.controller;

import com.hotel.model.User;
import com.hotel.repository.UserRepository;
import com.hotel.service.HotelService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final HotelService hotelService;

    public AuthController(UserRepository userRepository, HotelService hotelService) {
        this.userRepository = userRepository;
        this.hotelService = hotelService;
    }

    @GetMapping("/")
    public String mainPage(Model model, HttpSession session) {
        model.addAttribute("hotels", hotelService.getHotels());
        model.addAttribute("sessionUser", session.getAttribute("user"));
        return "index";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String name, @RequestParam String email,
                               @RequestParam String password, @RequestParam String role) {
        userRepository.save(new User(name, email, password, role.toUpperCase()));
        return "redirect:/login";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String email, @RequestParam String password, HttpSession session) {
        User user = userRepository.findByEmailAndPassword(email, password).orElse(null);
        if (user != null) {
            session.setAttribute("user", user);
            if ("OWNER".equals(user.getRole())) {
                return "redirect:/owner/dashboard";
            } else {
                return "redirect:/customer/dashboard";
            }
        }
        return "redirect:/login?error=true";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}