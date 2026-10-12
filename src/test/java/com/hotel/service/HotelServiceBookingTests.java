package com.hotel.service;

import com.hotel.model.Booking;
import com.hotel.model.Hotel;
import com.hotel.repository.BookingRepository;
import com.hotel.repository.HotelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HotelServiceBookingTests {

    @Mock
    private HotelRepository hotelRepository;

    @Mock
    private BookingRepository bookingRepository;

    private HotelService hotelService;
    private Hotel hotel;

    @BeforeEach
    void setUp() {
        hotelService = new HotelService(hotelRepository, bookingRepository);
        hotel = new Hotel("Test Hotel", "Colombo", 10000, 20L, "", "");
    }

    @Test
    void createsBookingWithGuestCountAndTotalPrice() {
        LocalDate checkIn = LocalDate.now().plusDays(2);
        LocalDate checkOut = checkIn.plusDays(3);
        when(hotelRepository.findByIdForBooking(10L)).thenReturn(Optional.of(hotel));
        when(bookingRepository.findByHotelId(10L)).thenReturn(List.of());

        assertTrue(hotelService.createBooking(1L, 10L, checkIn, checkOut, 2));

        var bookingCaptor = forClass(Booking.class);
        verify(bookingRepository).save(bookingCaptor.capture());
        assertEquals(2, bookingCaptor.getValue().getGuestCount());
        assertEquals(30000L, bookingCaptor.getValue().getTotalPrice());
    }

    @Test
    void rejectsInvalidDatesPastDatesAndInvalidGuestCount() {
        LocalDate today = LocalDate.now();
        assertFalse(hotelService.createBooking(1L, 10L, today, today, 1));
        assertFalse(hotelService.createBooking(1L, 10L, today.minusDays(1), today.plusDays(1), 1));
        assertFalse(hotelService.createBooking(1L, 10L, today.plusDays(1), today.plusDays(2), 0));

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void rejectsOverlappingBookingButAllowsAdjacentCheckoutAndCheckin() {
        LocalDate existingCheckIn = LocalDate.now().plusDays(3);
        Booking existing = new Booking(2L, 10L, hotel.getName(), existingCheckIn,
                existingCheckIn.plusDays(2), 1, 20000L, "BOOKED");
        existing.setId(50L);
        when(hotelRepository.findByIdForBooking(10L)).thenReturn(Optional.of(hotel));
        when(bookingRepository.findByHotelId(10L)).thenReturn(List.of(existing));

        assertFalse(hotelService.createBooking(1L, 10L, existingCheckIn.plusDays(1),
                existingCheckIn.plusDays(3), 1));
        assertTrue(hotelService.createBooking(1L, 10L, existingCheckIn.plusDays(2),
                existingCheckIn.plusDays(4), 1));
    }

    @Test
    void onlyOwningCustomerCanUpdateActiveBooking() {
        LocalDate checkIn = LocalDate.now().plusDays(4);
        Booking booking = new Booking(1L, 10L, hotel.getName(), checkIn,
                checkIn.plusDays(2), 1, 20000L, "BOOKED");
        booking.setId(60L);
        when(bookingRepository.findById(60L)).thenReturn(Optional.of(booking));

        assertFalse(hotelService.updateBooking(2L, 60L, checkIn.plusDays(1),
                checkIn.plusDays(3), 1));
        verify(hotelRepository, never()).findByIdForBooking(10L);
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void ownerCanUpdateOwnBookingDatesAndGuestCount() {
        LocalDate checkIn = LocalDate.now().plusDays(4);
        Booking booking = new Booking(1L, 10L, hotel.getName(), checkIn,
                checkIn.plusDays(2), 1, 20000L, "BOOKED");
        booking.setId(60L);
        when(bookingRepository.findById(60L)).thenReturn(Optional.of(booking));
        when(hotelRepository.findByIdForBooking(10L)).thenReturn(Optional.of(hotel));
        when(bookingRepository.findByHotelId(10L)).thenReturn(List.of(booking));

        assertTrue(hotelService.updateBooking(1L, 60L, checkIn.plusDays(1),
                checkIn.plusDays(4), 3));

        verify(bookingRepository).save(booking);
    }

    @Test
    void cancellationIsOwnedAndOnlySucceedsOnce() {
        LocalDate checkIn = LocalDate.now().plusDays(4);
        Booking booking = new Booking(1L, 10L, hotel.getName(), checkIn,
                checkIn.plusDays(2), 1, 20000L, "BOOKED");
        booking.setId(70L);
        when(bookingRepository.findById(70L)).thenReturn(Optional.of(booking));

        assertFalse(hotelService.cancelBooking(2L, 70L));
        assertTrue(hotelService.cancelBooking(1L, 70L));
        assertFalse(hotelService.cancelBooking(1L, 70L));
    }

    @Test
    void cancellationIsNotAllowedOnOrAfterCheckin() {
        LocalDate today = LocalDate.now();
        Booking booking = new Booking(1L, 10L, hotel.getName(), today,
                today.plusDays(2), 1, 20000L, "BOOKED");
        booking.setId(71L);
        when(bookingRepository.findById(71L)).thenReturn(Optional.of(booking));

        assertFalse(hotelService.cancelBooking(1L, 71L));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void cancelledBookingCannotBeUpdated() {
        LocalDate checkIn = LocalDate.now().plusDays(4);
        Booking booking = new Booking(1L, 10L, hotel.getName(), checkIn,
                checkIn.plusDays(2), 1, 20000L, "CANCELLED");
        booking.setId(80L);
        when(bookingRepository.findById(80L)).thenReturn(Optional.of(booking));

        assertFalse(hotelService.updateBooking(1L, 80L, checkIn.plusDays(1),
                checkIn.plusDays(3), 1));
        verify(bookingRepository, never()).save(any(Booking.class));
    }
}
