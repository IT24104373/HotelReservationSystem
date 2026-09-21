package com.hotel.service;

import com.hotel.model.Booking;
import com.hotel.model.Hotel;
import com.hotel.repository.BookingRepository;
import com.hotel.repository.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class HotelService {

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private BookingRepository bookingRepository;

    public List<Hotel> getHotels() {
        return hotelRepository.findAll();
    }

    public List<Hotel> getHotels(Long ownerId) {
        return hotelRepository.findByOwnerId(ownerId);
    }

    // Hotel Add with Minimum 1000 Validation
    public boolean saveHotel(Hotel hotel) {
        if (hotel.getPricePerNight() < 1000) {
            return false; // Minimum price LKR 1000
        }
        hotelRepository.save(hotel);
        return true;
    }

    public void deleteHotel(Long hotelId) {
        hotelRepository.deleteById(hotelId);
    }

    // Availability Check for Date Range
    public boolean isHotelAvailable(Long hotelId, String checkIn, String checkOut) {
        List<Booking> existingBookings = bookingRepository.findByHotelId(hotelId);
        LocalDate newIn = LocalDate.parse(checkIn);
        LocalDate newOut = LocalDate.parse(checkOut);

        for (Booking b : existingBookings) {
            if ("BOOKED".equalsIgnoreCase(b.getStatus())) {
                LocalDate existIn = LocalDate.parse(b.getCheckInDate());
                LocalDate existOut = LocalDate.parse(b.getCheckOutDate());

                // Check for Date Overlap
                if (newIn.isBefore(existOut) && newOut.isAfter(existIn)) {
                    return false; // Already booked for this range
                }
            }
        }
        return true;
    }

    public boolean createBooking(Long customerId, Long hotelId, String checkIn, String checkOut) {
        if (!isHotelAvailable(hotelId, checkIn, checkOut)) {
            return false; // Cannot book, range overlaps
        }
        Hotel hotel = hotelRepository.findById(hotelId).orElse(null);
        if (hotel != null) {
            Booking booking = new Booking(customerId, hotelId, hotel.getName(), checkIn, checkOut, "BOOKED");
            bookingRepository.save(booking);
            return true;
        }
        return false;
    }

    public List<Booking> getCustomerBookings(Long customerId) {
        return bookingRepository.findByCustomerId(customerId);
    }

    public boolean updateBookingDate(Long bookingId, String newIn, String newOut) {
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking != null) {
            if (isHotelAvailable(booking.getHotelId(), newIn, newOut)) {
                booking.setCheckInDate(newIn);
                booking.setCheckOutDate(newOut);
                bookingRepository.save(booking);
                return true;
            }
        }
        return false;
    }

    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking != null) {
            booking.setStatus("CANCELLED");
            bookingRepository.save(booking);
        }
    }

    public List<Booking> getBookingsForHotel(Long hotelId) {
        return bookingRepository.findByHotelId(hotelId);
    }
}