package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.example.demo.controllers.DoctorController;
import com.example.demo.models.Doctor;
import com.example.demo.repositories.DoctorRepository;

import java.util.ArrayList;
import java.util.List;

public class DoctorControllerTest {

    @Test
    public void testLogin() {
        DoctorController controller = new DoctorController();
        ModelAndView mav = controller.login();
        assertEquals("/doctors/loginDoctor.html", mav.getViewName());
        assertEquals(new Doctor(), mav.getModel().get("doctor"));
    }

    @Test
    public void testLoginProgressSuccess() {
        DoctorRepository doctorRepositoryMock = mock(DoctorRepository.class);
        HttpSession sessionMock = mock(HttpSession.class);
        Doctor doctor = new Doctor();
        doctor.setEmail("test@example.com");
        doctor.setPassword(BCrypt.hashpw("password", BCrypt.gensalt())); // Hash the password
        

        when(doctorRepositoryMock.findByEmail("test@example.com")).thenReturn(doctor);
        when(sessionMock.getAttribute("name")).thenReturn(doctor.getName());
        when(sessionMock.getAttribute("email")).thenReturn(doctor.getEmail());
        when(sessionMock.getAttribute("phonenumber")).thenReturn(doctor.getPhonenumber());
        when(sessionMock.getAttribute("doctor_id")).thenReturn(doctor.getId());

        DoctorController controller = new DoctorController();
        controller.doctorRepository = doctorRepositoryMock;

        RedirectView redirectView = controller.loginProgress("test@example.com", "password", sessionMock);
        assertEquals("Profile", redirectView.getUrl());
    }

    @Test
    public void testLoginProgressFailure() {
        DoctorRepository doctorRepositoryMock = mock(DoctorRepository.class);
        HttpSession sessionMock = mock(HttpSession.class);

        when(doctorRepositoryMock.findByEmail(anyString())).thenReturn(null);

        DoctorController controller = new DoctorController();
        controller.doctorRepository = doctorRepositoryMock;

        RedirectView redirectView = controller.loginProgress("test@example.com", "password", sessionMock);
        assertEquals("login", redirectView.getUrl());
    }

    @Test
    public void testViewProfile() {
        HttpSession sessionMock = mock(HttpSession.class);

        Doctor doctor = new Doctor();
        doctor.setName("Test Doctor");
        doctor.setEmail("test@example.com");
        doctor.setPhonenumber("1234567890");
        doctor.setPassword("password");
        doctor.setId(1L);

        when(sessionMock.getAttribute("doctor_id")).thenReturn(doctor.getId());
        when(sessionMock.getAttribute("name")).thenReturn(doctor.getName());
        when(sessionMock.getAttribute("email")).thenReturn(doctor.getEmail());
        when(sessionMock.getAttribute("phonenumber")).thenReturn(doctor.getPhonenumber());
        when(sessionMock.getAttribute("password")).thenReturn(doctor.getPassword());

        DoctorController controller = new DoctorController();

        ModelAndView mav = controller.viewProfile(sessionMock);

        assertEquals("/doctors/ProfileDoctor.html", mav.getViewName());
        assertEquals(doctor.getName(), mav.getModel().get("name"));
        assertEquals(doctor.getEmail(), mav.getModel().get("email"));
        assertEquals(doctor.getPhonenumber(), mav.getModel().get("phonenumber"));
        assertEquals(doctor.getPassword(), mav.getModel().get("password"));
        assertEquals(doctor.getId(), mav.getModel().get("id"));
    }

    @Test
public void testEditProfile() {
    DoctorRepository doctorRepositoryMock = mock(DoctorRepository.class);
    HttpSession sessionMock = mock(HttpSession.class);

    Doctor existingDoctor = new Doctor();
    existingDoctor.setName("Existing Doctor");
    existingDoctor.setEmail("existing@example.com");
    existingDoctor.setPhonenumber("9876543210");
    existingDoctor.setPassword("existing_password");
    existingDoctor.setId(1L);

    when(sessionMock.getAttribute("name")).thenReturn(existingDoctor.getName());
    when(doctorRepositoryMock.findByName(existingDoctor.getName())).thenReturn(existingDoctor);

    Doctor updatedDoctor = new Doctor();
    updatedDoctor.setName("Updated Doctor");
    updatedDoctor.setEmail("updated@example.com");
    updatedDoctor.setPhonenumber("1234567890");
    String newPassword = "new_password";

    DoctorController controller = new DoctorController();
    controller.doctorRepository = doctorRepositoryMock;

    RedirectView redirectView = controller.editProfile(updatedDoctor, newPassword, sessionMock);
    assertEquals("/doctor/Profile", redirectView.getUrl());

    // Verify that the existing doctor's information is updated
    assertEquals(updatedDoctor.getName(), existingDoctor.getName());
    assertEquals(updatedDoctor.getEmail(), existingDoctor.getEmail());
    assertEquals(updatedDoctor.getPhonenumber(), existingDoctor.getPhonenumber());
    assertEquals(newPassword, existingDoctor.getPassword()); // Password should be updated
}


    
}
