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
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.controllers.AppointmentController;
import com.example.demo.models.Appointment;
import com.example.demo.models.Doctor;
import com.example.demo.repositories.AppointmentRepository;
import com.example.demo.repositories.DoctorRepository;

import jakarta.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;

class AppointmentControllerTest {

    @InjectMocks
    private AppointmentController appointmentController;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private HttpServletResponse response;

    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        session = new MockHttpSession();
    }

    @Test
    void testViewAppointment() {
        List<Appointment> appointments = new ArrayList<>();
        when(appointmentRepository.findByDoctorId(anyLong())).thenReturn(appointments);

        ModelAndView mav = appointmentController.viewAppointment(1L);
        assertEquals("/user/booking.html", mav.getViewName());
        assertTrue(mav.getModel().containsKey("appointments"));
        assertEquals(appointments, mav.getModel().get("appointments"));
    }

    @Test
    void testAddAppointment() {
        ModelAndView mav = appointmentController.addAppointment();
        assertEquals("/doctors/addAppointments.html", mav.getViewName());
        assertTrue(mav.getModel().containsKey("appointments"));
    }

    @Test
    void testSaveAppointment() throws IOException {
        Appointment appointment = new Appointment();
        session.setAttribute("doctor_id", 1L);
        Doctor doctor = new Doctor();
        doctor.setId(1L);
        when(doctorRepository.findById(anyLong())).thenReturn(Optional.of(doctor));

        appointmentController.saveAppointment(appointment, response, session);

        verify(appointmentRepository, times(1)).save(appointment);
        verify(response, times(1)).sendRedirect("add-appointment");
    }

    @Test
    void testViewAppointments() throws IOException {
        List<Appointment> appointments = new ArrayList<>();
        session.setAttribute("doctor_id", 1L);
        when(appointmentRepository.findByDoctorId(anyLong())).thenReturn(appointments);

        ModelAndView mav = appointmentController.viewAppointments(session, response);

        assertEquals("/doctors/appointments.html", mav.getViewName());
        assertTrue(mav.getModel().containsKey("appointments"));
        assertEquals(appointments, mav.getModel().get("appointments"));
    }

    @Test
    void testViewAllAppointments() throws IOException {
        List<Appointment> appointments = new ArrayList<>();
        when(appointmentRepository.findAll()).thenReturn(appointments);

        ModelAndView mav = appointmentController.viewAllAppointments();
        assertEquals("/admin/viewAppointments.html", mav.getViewName());
        assertTrue(mav.getModel().containsKey("appointments"));
        assertEquals(appointments, mav.getModel().get("appointments"));
    }

    @Test
    void testDeleteAppointment() throws IOException {
        Appointment appointment = new Appointment();
        when(appointmentRepository.findById(anyInt())).thenReturn(Optional.of(appointment));

        appointmentController.deleteAppointment(1, response, session);

        verify(appointmentRepository, times(1)).delete(appointment);
        verify(response, times(1)).sendRedirect("/appointments/my-appointments");
    }

    @Test
    void testViewEditForm() {
        Appointment appointment = new Appointment();
        when(appointmentRepository.findById(anyInt())).thenReturn(Optional.of(appointment));

        ModelAndView mav = appointmentController.viewEditForm(1);
        assertEquals("/doctors/editAppointment", mav.getViewName());
        assertTrue(mav.getModel().containsKey("appointment"));
        assertEquals(appointment, mav.getModel().get("appointment"));
    }

    @Test
    void testEditAppointment() throws IOException {
        Appointment appointment = new Appointment();
        session.setAttribute("doctor_id", 1L);
        when(appointmentRepository.findById(anyInt())).thenReturn(Optional.of(appointment));

        appointmentController.editAppointment(1, appointment, session, response);

        verify(appointmentRepository, times(1)).save(appointment);
        verify(response, times(1)).sendRedirect("/appointments/my-appointments");
    }
}
