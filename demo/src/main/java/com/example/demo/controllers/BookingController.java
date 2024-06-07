package com.example.demo.controllers;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.models.Appointment;
import com.example.demo.models.Booking;
import com.example.demo.models.User;
import com.example.demo.repositories.AppointmentRepository;
import com.example.demo.repositories.BookingRepository;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/user/booking")
public class BookingController {

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    AppointmentRepository appointmentRepository;

    String message = null;

    @GetMapping("booked")
    public void book(@RequestParam int id, HttpServletResponse response, HttpSession session) throws IOException {
        Appointment appointment = appointmentRepository.findById(id).get();
        Booking booking = new Booking();
        Long userId = (Long) session.getAttribute("user_id");
        Boolean exists = bookingRepository.existsByUserId(userId);

        if (exists) {
            this.message = "Can't Book Another Appointment!, You already have a booked appointment.";
            response.sendRedirect("/user/booking/my-bookings");
        }

        else {
            if (userId != null) {
                User user = new User();
                user.setId(userId);
                booking.setUser(user);
                booking.setDate(appointment.getDate());
                booking.setStartHr(appointment.getStartHr());
                booking.setEndHr(appointment.getEndHr());
                booking.setDoctor(appointment.getDoctor());
                this.bookingRepository.save(booking);
                this.appointmentRepository.delete(appointment);
                this.message = "Your booking has been successfully saved";
                response.sendRedirect("/user/booking/my-bookings");
            } else {
                response.sendRedirect("/user/Login");
            }
        }
    }

    @GetMapping("my-bookings")
    public ModelAndView viewBookings(HttpSession session, HttpServletResponse response) throws IOException {
        ModelAndView mav = new ModelAndView("/user/bookingList.html");
        Long userId = (Long) session.getAttribute("user_id");
        if (userId != null) {
            Boolean exists = bookingRepository.existsByUserId(userId);
            if (exists) {
                Booking booking = this.bookingRepository.findByUserId(userId);
                mav.addObject("booking", booking);
            } else {
                mav.addObject("booking", null);
                this.message = "You don't have any booked appointments";
            }
        } else {
            response.sendRedirect("/user/Login");
        }

        mav.addObject("message", this.message);
        this.message = null;

        return mav;
    }

    @GetMapping("/doctor/my-bookings")
    public ModelAndView viewDoctorBookings(HttpSession session, HttpServletResponse response) throws IOException {
        ModelAndView mav = new ModelAndView("/doctors/bookingList.html");
        Long doctorId = (Long) session.getAttribute("doctor_id");
        if (doctorId != null) {
            Boolean exists = bookingRepository.existsByDoctorId(doctorId);
            if (exists) {
                Booking booking = this.bookingRepository.findByDoctorId(doctorId);
                mav.addObject("booking", booking);
            } else {
                mav.addObject("booking", null);
                mav.addObject("message", "You don't have any booked appointments");
            }
        }
        return mav;
    }

    @GetMapping("/admin/all-bookings")
    public ModelAndView viewAllBookings() throws IOException {
        ModelAndView mav = new ModelAndView("/admin/viewBookings.html");
        List<Booking> bookings = this.bookingRepository.findAll();
        mav.addObject("bookings", bookings);
        return mav;
    }

    @GetMapping("cancelled")
    public void cancelBooking(@RequestParam int id, HttpServletResponse response, HttpSession session)
            throws IOException {
        Booking booking = bookingRepository.findById(id).get();
        Appointment appointment = new Appointment();
        appointment.setDate(booking.getDate());
        appointment.setStartHr(booking.getStartHr());
        appointment.setEndHr(booking.getEndHr());
        appointment.setDoctor(booking.getDoctor());
        this.appointmentRepository.save(appointment);
        this.bookingRepository.delete(booking);
        this.message = "You have cancelled your booking";
        response.sendRedirect("/user/booking/my-bookings");
    }

}
