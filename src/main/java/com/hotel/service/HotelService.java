package com.hotel.service;

import com.hotel.model.Booking;
import com.hotel.model.Hotel;
import com.hotel.repository.BookingRepository;
import com.hotel.repository.HotelRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * ============================================================================
 * SERVICES: Hotel & Booking Business Logic
 * - MEMBER 1: Hotel Management (Full CRUD)
 * - MEMBER 2: Booking Management (Full CRUD)
 * 
 * OOP Concepts:
 * - Polymorphism: Method Overloading with getHotels()
 * - Encapsulation: Date & Price validations inside methods
 * ============================================================================
 */
@Service
public class HotelService {

    private final HotelRepository hotelRepository;
    private final BookingRepository bookingRepository;

    public HotelService(HotelRepository hotelRepository, BookingRepository bookingRepository) {
        this.hotelRepository = hotelRepository;
        this.bookingRepository = bookingRepository;
    }

    // =========================================================================
    // MEMBER 1: HOTEL MANAGEMENT (CRUD OPERATIONS)
    // =========================================================================

    // 1. CREATE: Save a new hotel with validation (Min LKR 1000 & multiples of LKR 500)
    public boolean saveHotel(Hotel hotel) {
        if (hotel.getPricePerNight() < 1000 || hotel.getPricePerNight() % 500 != 0) {
            return false;
        }
        hotelRepository.save(hotel);
        return true;
    }

    // 2. READ: Polymorphism (Method Overloading - Version 1: Get all hotels)
    public List<Hotel> getHotels() {
        return hotelRepository.findAll();
    }

    // 2. READ: Polymorphism (Method Overloading - Version 2: Get hotels by owner ID)
    public List<Hotel> getHotels(Long ownerId) {
        return hotelRepository.findByOwnerId(ownerId);
    }

    // 3. UPDATE: Update hotel details (Min LKR 1000 & multiples of LKR 500)
    public boolean updateHotel(Long hotelId, String name, String location, long pricePerNight, String imageUrl, String description) {
        if (pricePerNight < 1000 || pricePerNight % 500 != 0) {
            return false;
        }
        Hotel hotel = hotelRepository.findById(hotelId).orElse(null);
        if (hotel != null) {
            hotel.setName(name);
            hotel.setLocation(location);
            hotel.setPricePerNight(pricePerNight);
            hotel.setImageUrl(imageUrl);
            hotel.setDescription(description);
            hotelRepository.save(hotel);
            return true;
        }
        return false;
    }

    // 4. DELETE: Delete a hotel by ID
    public void deleteHotel(Long hotelId) {
        hotelRepository.deleteById(hotelId);
    }

    // =========================================================================
    // MEMBER 2: BOOKING MANAGEMENT (CRUD OPERATIONS)
    // =========================================================================

    // 1. CREATE: Create a new reservation with overlap check
    public boolean createBooking(Long customerId, Long hotelId, LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null || !checkOut.isAfter(checkIn)) {
            return false;
        }

        // Check if dates conflict with an existing confirmed booking
        List<Booking> existing = bookingRepository.findByHotelId(hotelId);
        for (Booking b : existing) {
            if ("BOOKED".equals(b.getStatus())) {
                boolean overlap = !(checkOut.isBefore(b.getCheckInDate()) || checkIn.isAfter(b.getCheckOutDate()));
                if (overlap) return false;
            }
        }

        Hotel hotel = hotelRepository.findById(hotelId).orElse(null);
        if (hotel == null) return false;

        Booking booking = new Booking(customerId, hotelId, hotel.getName(), checkIn, checkOut, "BOOKED");
        bookingRepository.save(booking);
        return true;
    }

    // 2. READ: Get bookings by customer ID
    public List<Booking> getCustomerBookings(Long customerId) {
        return bookingRepository.findByCustomerId(customerId);
    }

    // 2. READ: Get bookings by hotel ID (for owner view)
    public List<Booking> getBookingsForHotel(Long hotelId) {
        return bookingRepository.findByHotelId(hotelId);
    }

    // 3. UPDATE: Reschedule / Update booking dates with overlap check
    public boolean updateBooking(Long bookingId, LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null || !checkOut.isAfter(checkIn)) {
            return false;
        }

        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking == null) return false;

        List<Booking> existing = bookingRepository.findByHotelId(booking.getHotelId());
        for (Booking b : existing) {
            if ("BOOKED".equals(b.getStatus()) && !b.getId().equals(bookingId)) {
                boolean overlap = !(checkOut.isBefore(b.getCheckInDate()) || checkIn.isAfter(b.getCheckOutDate()));
                if (overlap) return false;
            }
        }

        booking.setCheckInDate(checkIn);
        booking.setCheckOutDate(checkOut);
        bookingRepository.save(booking);
        return true;
    }

    // 4. DELETE / CANCEL: Cancel a reservation
    public void cancelBooking(Long bookingId) {
        Booking b = bookingRepository.findById(bookingId).orElse(null);
        if (b != null) {
            b.setStatus("CANCELLED");
            bookingRepository.save(b);
        }
    }
}