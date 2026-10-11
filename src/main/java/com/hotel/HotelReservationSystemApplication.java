package com.hotel;

import com.hotel.model.Booking;
import com.hotel.model.Hotel;
import com.hotel.model.Review;
import com.hotel.model.User;
import com.hotel.repository.BookingRepository;
import com.hotel.repository.HotelRepository;
import com.hotel.repository.ReviewRepository;
import com.hotel.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.util.List;

/**
 * Main Spring Boot Application with initial demo data seeder.
 * Provides sample data for all 3 Student Modules:
 * - Member 1: Hotels
 * - Member 2: Bookings
 * - Member 3: Reviews & Ratings
 */
@SpringBootApplication
public class HotelReservationSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(HotelReservationSystemApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(UserRepository userRepository,
                                      HotelRepository hotelRepository,
                                      BookingRepository bookingRepository,
                                      ReviewRepository reviewRepository) {
        return args -> {
            if (hotelRepository.count() == 0) {
                // 1. Seed Demo Accounts
                User owner = new User("Nimal Fernando", "owner@hotel.com", "123456", "OWNER");
                userRepository.save(owner);

                User customer = new User("Kamal Perera", "customer@hotel.com", "123456", "CUSTOMER");
                userRepository.save(customer);

                // 2. Member 1: Seed Hotels
                Hotel h1 = new Hotel(
                        "Amanwella Ocean Sanctuary",
                        "Tangalle, Southern Province",
                        45000,
                        owner.getId(),
                        "https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=1200&q=80",
                        "Set among mature coconut groves facing a crescent-shaped beach, Amanwella is a contemporary beach resort offering private plunge pools and uninterrupted ocean panoramas."
                );

                Hotel h2 = new Hotel(
                        "Heritance Kandalama Forest Retreat",
                        "Dambulla, Cultural Triangle",
                        38500,
                        owner.getId(),
                        "https://images.unsplash.com/photo-1582719508461-905c673771fd?auto=format&fit=crop&w=1200&q=80",
                        "An architectural masterpiece designed by Geoffrey Bawa, nestled beneath a cliff face overlooking Kandalama Lake, surrounded by lush tropical biodiversity."
                );

                Hotel h3 = new Hotel(
                        "The Grand Heritage Resort",
                        "Nuwara Eliya, Central Highlands",
                        28000,
                        owner.getId(),
                        "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?auto=format&fit=crop&w=1200&q=80",
                        "Experience colonial elegance in the misty hills of Little England. Featuring manicured rose gardens, warm log fires, high tea pavilions, and award-winning dining."
                );

                Hotel h4 = new Hotel(
                        "Cape Weligama Luxury Cliff Villas",
                        "Weligama Bay, Southern Coast",
                        52000,
                        owner.getId(),
                        "https://images.unsplash.com/photo-1571896349842-33c89424de2d?auto=format&fit=crop&w=1200&q=80",
                        "Perched atop a rugged cliff towering 40 meters above the Indian Ocean, featuring iconic crescent-shaped infinity pools, bespoke butler service, and private dining."
                );

                hotelRepository.saveAll(List.of(h1, h2, h3, h4));

                // 3. Member 2: Seed Sample Booking
                Booking b1 = new Booking(
                        customer.getId(),
                        h1.getId(),
                        h1.getName(),
                        LocalDate.now().plusDays(3),
                        LocalDate.now().plusDays(6),
                        "BOOKED"
                );
                bookingRepository.save(b1);

                // 4. Member 3: Seed Sample Review
                Review r1 = new Review(
                        customer.getId(),
                        customer.getName(),
                        h1.getId(),
                        h1.getName(),
                        5,
                        "Outstanding ocean views, incredible infinity pool, and world-class hospitality! Highly recommended."
                );
                reviewRepository.save(r1);
            }
        };
    }
}