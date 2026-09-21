package com.hotel.controller;

import com.hotel.model.Customer;
import com.hotel.model.HotelOwner;
import com.hotel.model.User;
import com.hotel.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
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
        if ("OWNER".equalsIgnoreCase(role)) {
            userRepository.save(new HotelOwner(name, email, password));
        } else {
            userRepository.save(new Customer(name, email, password));
        }
        return "redirect:/";
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
        return "redirect:/?error=true";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}