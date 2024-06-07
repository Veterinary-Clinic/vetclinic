package com.example.demo.controllers;

import com.example.demo.models.Admin;
import com.example.demo.models.Appointment;
import com.example.demo.models.Booking;
import com.example.demo.models.Doctor;
import com.example.demo.repositories.AppointmentRepository;
import com.example.demo.repositories.DoctorRepository;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/appointments")

public class AppointmentController {
    @Autowired
    AppointmentRepository appointmentRepository;

    @Autowired
    DoctorRepository doctorRepository;

    @GetMapping("available-appointments")
    public ModelAndView viewAppointment(@RequestParam long id) {
        ModelAndView mav = new ModelAndView("/user/booking.html");
        List<Appointment> appointments = this.appointmentRepository.findByDoctorId(id);
        mav.addObject("appointments", appointments);
        return mav;
    }

    @GetMapping("add-appointment")
    public ModelAndView addAppointment() {
        ModelAndView mav = new ModelAndView("/doctors/addAppointments.html");
        Appointment appointment = new Appointment();
        // mav.addObject("", id); // Add doctor's ID to the ModelAndView
        mav.addObject("appointments", appointment);
        return mav;
    }

    @PostMapping("save-appointment")
    public void saveAppointment(@ModelAttribute Appointment appointment, HttpServletResponse response,
            HttpSession session) throws IOException {
        Long doctorId = (Long) session.getAttribute("doctor_id");
        Doctor doctor = new Doctor();
        if (doctorId != null) {
            doctor.setId(doctorId);
            appointment.setDoctor(doctor);
            appointment.setStartHr(appointment.getStartHr());
            appointment.setEndHr(appointment.getEndHr());
            this.appointmentRepository.save(appointment);
            response.sendRedirect("add-appointment");
        } else {
            response.sendRedirect("/doctor/login");
        }
    }

    @GetMapping("my-appointments")
    public ModelAndView viewAppointments(HttpSession session, HttpServletResponse response) throws IOException {
        ModelAndView mav = new ModelAndView("/doctors/appointments.html");
        Long doctorId = (Long) session.getAttribute("doctor_id");
        if (doctorId != null) {
            List<Appointment> appointments = this.appointmentRepository.findByDoctorId(doctorId);
            mav.addObject("appointments", appointments);
        } else {
            response.sendRedirect("/doctor/login");
        } 
        return mav;
    }
    @GetMapping("/admin/all-appointments")
    public ModelAndView viewAllAppointments() throws IOException {
        ModelAndView mav = new ModelAndView("/admin/viewAppointments.html");
        List<Appointment> appointments = this.appointmentRepository.findAll();
        mav.addObject("appointments", appointments);
        return mav;
    }

    @GetMapping("delete")
    public void deleteAppointment(@RequestParam int id, HttpServletResponse response, HttpSession session)
            throws IOException {
        Appointment appointment = appointmentRepository.findById(id).get();
        this.appointmentRepository.delete(appointment);
        response.sendRedirect("/appointments/my-appointments");
    }

    @GetMapping("edit-appointment/{id}")
    public ModelAndView viewEditForm(@PathVariable int id) {
        ModelAndView mav = new ModelAndView("/doctors/editAppointment");
        Appointment appointment = appointmentRepository.findById(id).get();
        mav.addObject("appointment", appointment);
        return mav;
    }

    @PostMapping("edit-appointment/{id}")
    public void editAppointment(@PathVariable int id, @ModelAttribute Appointment updatedAppointment,
            HttpSession session, HttpServletResponse response) throws IOException {
        Appointment existingAppointment = appointmentRepository.findById(id).get();
        Long doctorId = (Long) session.getAttribute("doctor_id");
        existingAppointment.setId(id);
        existingAppointment.setDate(updatedAppointment.getDate());
        existingAppointment.setStartHr(updatedAppointment.getStartHr());
        existingAppointment.setEndHr(updatedAppointment.getEndHr());
        existingAppointment.setDoctorId(doctorId);
        this.appointmentRepository.save(existingAppointment);
        response.sendRedirect("/appointments/my-appointments");

    }

}