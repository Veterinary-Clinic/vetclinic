package com.example.demo;



import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.controllers.BookingController;
import com.example.demo.models.Appointment;
import com.example.demo.models.Booking;

import com.example.demo.repositories.AppointmentRepository;
import com.example.demo.repositories.BookingRepository;

import jakarta.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;

class BookingControllerTest {

    @InjectMocks
    private BookingController bookingController;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private HttpServletResponse response;

    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        session = new MockHttpSession();
    }

    @Test
    void testBookWithExistingBooking() throws IOException {
        Appointment appointment = new Appointment();
        when(appointmentRepository.findById(anyInt())).thenReturn(Optional.of(appointment));
        when(bookingRepository.existsByUserId(anyLong())).thenReturn(true);

        session.setAttribute("user_id", 1L);

        bookingController.book(1, response, session);

        verify(response, times(1)).sendRedirect("/user/booking/my-bookings");
        verify(bookingRepository, never()).save(any(Booking.class));
        verify(appointmentRepository, never()).delete(any(Appointment.class));
    }

    @Test
    void testBookWithoutExistingBooking() throws IOException {
        Appointment appointment = new Appointment();
        appointment.setId(1);
        when(appointmentRepository.findById(anyInt())).thenReturn(Optional.of(appointment));
        when(bookingRepository.existsByUserId(anyLong())).thenReturn(false);

        session.setAttribute("user_id", 1L);

        bookingController.book(1, response, session);

        verify(bookingRepository, times(1)).save(any(Booking.class));
        verify(appointmentRepository, times(1)).delete(any(Appointment.class));
        verify(response, times(1)).sendRedirect("/user/booking/my-bookings");
    }

    @Test
    void testViewBookingsWithUser() throws IOException {
        Booking booking = new Booking();
        when(bookingRepository.existsByUserId(anyLong())).thenReturn(true);
        when(bookingRepository.findByUserId(anyLong())).thenReturn(booking);

        session.setAttribute("user_id", 1L);

        ModelAndView mav = bookingController.viewBookings(session, response);

        assertEquals("/user/bookingList.html", mav.getViewName());
        assertTrue(mav.getModel().containsKey("booking"));
        assertEquals(booking, mav.getModel().get("booking"));
    }

    @Test
    void testViewBookingsWithoutUser() throws IOException {
        when(bookingRepository.existsByUserId(anyLong())).thenReturn(false);

        session.setAttribute("user_id", 1L);

        ModelAndView mav = bookingController.viewBookings(session, response);

        assertEquals("/user/bookingList.html", mav.getViewName());
        assertTrue(mav.getModel().containsKey("booking"));
        assertNull(mav.getModel().get("booking"));
    }

    @Test
    void testViewDoctorBookingsWithExistingBookings() throws IOException {
        Booking booking = new Booking();
        when(bookingRepository.existsByDoctorId(anyLong())).thenReturn(true);
        when(bookingRepository.findByDoctorId(anyLong())).thenReturn(booking);

        session.setAttribute("doctor_id", 1L);

        ModelAndView mav = bookingController.viewDoctorBookings(session, response);

        assertEquals("/doctors/bookingList.html", mav.getViewName());
        assertTrue(mav.getModel().containsKey("booking"));
        assertEquals(booking, mav.getModel().get("booking"));
    }

    @Test
    void testViewDoctorBookingsWithoutExistingBookings() throws IOException {
        when(bookingRepository.existsByDoctorId(anyLong())).thenReturn(false);

        session.setAttribute("doctor_id", 1L);

        ModelAndView mav = bookingController.viewDoctorBookings(session, response);

        assertEquals("/doctors/bookingList.html", mav.getViewName());
        assertTrue(mav.getModel().containsKey("booking"));
        assertNull(mav.getModel().get("booking"));
    }

    @Test
    void testViewAllBookings() throws IOException {
        List<Booking> bookings = new ArrayList<>();
        when(bookingRepository.findAll()).thenReturn(bookings);

        ModelAndView mav = bookingController.viewAllBookings();

        assertEquals("/admin/viewBookings.html", mav.getViewName());
        assertTrue(mav.getModel().containsKey("bookings"));
        assertEquals(bookings, mav.getModel().get("bookings"));
    }

    @Test
    void testCancelBooking() throws IOException {
        Booking booking = new Booking();
        booking.setId(1);
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));

        bookingController.cancelBooking(1, response, session);

        verify(bookingRepository, times(1)).delete(any(Booking.class));
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
        verify(response, times(1)).sendRedirect("/user/booking/my-bookings");
    }
}
