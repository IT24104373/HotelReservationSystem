package com.hotel.service;

import com.hotel.model.Booking;
import com.hotel.model.Hotel;
import com.hotel.repository.BookingRepository;
import com.hotel.repository.HotelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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

    // 1. CREATE: Create a reservation with date and hotel availability checks
    @Transactional
    public boolean createBooking(Long customerId, Long hotelId, LocalDate checkIn,
                                 LocalDate checkOut, int guestCount) {
        if (customerId == null || hotelId == null || guestCount < 1 || !isValidStay(checkIn, checkOut)) {
            return false;
        }

        Hotel hotel = hotelRepository.findByIdForBooking(hotelId).orElse(null);
        if (hotel == null || hasBookingOverlap(hotelId, checkIn, checkOut, null)) return false;

        Long totalPrice = calculateTotalPrice(hotel, checkIn, checkOut);
        if (totalPrice == null) return false;

        Booking booking = new Booking(customerId, hotelId, hotel.getName(), checkIn,
                checkOut, guestCount, totalPrice, "BOOKED");
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

    // 3. UPDATE: Only the owning customer may update a future, active reservation
    @Transactional
    public boolean updateBooking(Long customerId, Long bookingId, LocalDate checkIn,
                                 LocalDate checkOut, int guestCount) {
        if (customerId == null || bookingId == null || guestCount < 1 || !isValidStay(checkIn, checkOut)) {
            return false;
        }

        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (!isOwnedActiveBooking(booking, customerId)) return false;

        Hotel hotel = hotelRepository.findByIdForBooking(booking.getHotelId()).orElse(null);
        if (hotel == null || hasBookingOverlap(booking.getHotelId(), checkIn, checkOut, bookingId)) return false;

        Long totalPrice = calculateTotalPrice(hotel, checkIn, checkOut);
        if (totalPrice == null) return false;

        booking.setCheckInDate(checkIn);
        booking.setCheckOutDate(checkOut);
        booking.setGuestCount(guestCount);
        booking.setTotalPrice(totalPrice);
        bookingRepository.save(booking);
        return true;
    }

    // 4. CANCEL: Keep the reservation record and only cancel future active bookings
    @Transactional
    public boolean cancelBooking(Long customerId, Long bookingId) {
        if (customerId == null || bookingId == null) return false;

        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (!isOwnedActiveBooking(booking, customerId)
                || booking.getCheckInDate() == null
                || !booking.getCheckInDate().isAfter(LocalDate.now())) {
            return false;
        }

        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);
        return true;
    }

    private boolean isValidStay(LocalDate checkIn, LocalDate checkOut) {
        return checkIn != null && checkOut != null
                && !checkIn.isBefore(LocalDate.now())
                && checkOut.isAfter(checkIn);
    }

    private boolean isOwnedActiveBooking(Booking booking, Long customerId) {
        return booking != null
                && customerId.equals(booking.getCustomerId())
                && "BOOKED".equals(booking.getStatus());
    }

    private boolean hasBookingOverlap(Long hotelId, LocalDate checkIn, LocalDate checkOut,
                                      Long excludedBookingId) {
        return bookingRepository.findByHotelId(hotelId).stream()
                .filter(booking -> "BOOKED".equals(booking.getStatus()))
                .filter(booking -> excludedBookingId == null || !excludedBookingId.equals(booking.getId()))
                .anyMatch(booking -> booking.getCheckInDate() != null
                        && booking.getCheckOutDate() != null
                        && checkIn.isBefore(booking.getCheckOutDate())
                        && checkOut.isAfter(booking.getCheckInDate()));
    }

    private Long calculateTotalPrice(Hotel hotel, LocalDate checkIn, LocalDate checkOut) {
        try {
            return Math.multiplyExact(hotel.getPricePerNight(), ChronoUnit.DAYS.between(checkIn, checkOut));
        } catch (ArithmeticException exception) {
            return null;
        }
    }
}