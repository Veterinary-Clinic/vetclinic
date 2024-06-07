package com.example.demo;

import com.example.demo.controllers.AdminController;
import com.example.demo.models.Admin;
import com.example.demo.models.Appointment;
import com.example.demo.models.Booking;
import com.example.demo.models.Doctor;
import com.example.demo.repositories.AdminRepository;
import com.example.demo.repositories.AppointmentRepository;
import com.example.demo.repositories.BookingRepository;
import com.example.demo.repositories.DoctorRepository;
import com.example.demo.repositories.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


public class AdminControllerTest {

    @InjectMocks
    private AdminController adminController;

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    

    @Test
    public void testIndexAdmins() {
        when(userRepository.count()).thenReturn(10L);
        List<Appointment> appointments = new ArrayList<>();
        when(appointmentRepository.findTop10ByOrderByDateDesc()).thenReturn(appointments);
        List<Booking> bookings = new ArrayList<>();
        when(bookingRepository.findByDateAfterOrDateEqualsOrderByDateDesc(any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(bookings);

        String viewName = adminController.indexAdmins(model);

        assertEquals("admin/index", viewName);
        verify(model, times(1)).addAttribute("patientCount", 10L);
        verify(model, times(1)).addAttribute("appointments", appointments);
        verify(model, times(1)).addAttribute("bookings", bookings);
    }
    @Test
    public void testCountPatients() {
        when(userRepository.count()).thenReturn(10L);

        String viewName = adminController.countPatients(model);

        assertEquals("admin/index", viewName);
        verify(model, times(1)).addAttribute("patientCount", 10L);
    }
    @Test
    public void testAddAdmin_Success() {
        Admin admin = new Admin();
        admin.setPassword("password123");
        when(bindingResult.hasErrors()).thenReturn(false);

        String viewName = adminController.addAdmin(admin, bindingResult);

        assertEquals("redirect:/admin/list", viewName);
        verify(adminRepository, times(1)).save(any(Admin.class));
    }
    @Test
    public void testAddAdmin_Failure() {
        Admin admin = new Admin();
        when(bindingResult.hasErrors()).thenReturn(true);

        String viewName = adminController.addAdmin(admin, bindingResult);

        assertEquals("admin/addAdmin", viewName);
        verify(adminRepository, times(0)).save(any(Admin.class));
    }
    @Test
    public void testLogin() {
        ModelAndView mav = adminController.login();

        assertEquals("/admin/loginAdmin.html", mav.getViewName());
        assertEquals(new Admin(), mav.getModel().get("admin"));
    }
    
    @Test
    public void testLoginProgress_Success() {
        Admin admin = new Admin();
        admin.setPassword(BCrypt.hashpw("password123", BCrypt.gensalt(12)));
        when(adminRepository.findByusername("admin")).thenReturn(admin);

        RedirectView view = adminController.loginProgress("admin", "password123", "1", session);

        assertEquals("index", view.getUrl());
        verify(session, times(1)).setAttribute("id", admin.getId());
        verify(session, times(1)).setAttribute("username", admin.getUsername());
    }

    @Test
    public void testLoginProgress_Failure() {
        when(adminRepository.findByusername("admin")).thenReturn(null);

        RedirectView view = adminController.loginProgress("admin", "wrongpassword", "1", session);

        assertEquals("login", view.getUrl());
        verify(session, times(0)).setAttribute(anyString(), any());
    }
    
    @Test
    public void testDeleteAdmin_Success() {
        Admin admin = new Admin();
        when(adminRepository.findById(anyLong())).thenReturn(Optional.of(admin));

        String viewName = adminController.deleteAdmin(1L);

        assertEquals("redirect:/admin/list", viewName);
        verify(adminRepository, times(1)).delete(admin);
    }

    @Test
    public void testDeleteAdmin_Failure() {
        when(adminRepository.findById(anyLong())).thenReturn(Optional.empty());

        try {
            adminController.deleteAdmin(1L);
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid doctor ID: 1", e.getMessage());
        }
    }
    @Test
    public void testShowEditAdminForm_Success() {
        Admin admin = new Admin();
        when(adminRepository.findById(anyLong())).thenReturn(Optional.of(admin));

        String viewName = adminController.showEditAdminForm(1L, model);

        assertEquals("admin/editAdmins", viewName);
        verify(model, times(1)).addAttribute("admin", admin);
    }

    @Test
    public void testShowEditAdminForm_Failure() {
        when(adminRepository.findById(anyLong())).thenReturn(Optional.empty());

        try {
            adminController.showEditAdminForm(1L, model);
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid admin ID: 1", e.getMessage());
        }
    }

    @Test
    public void testUpdateAdmin_Success() {
        Admin admin = new Admin();
        when(adminRepository.findById(anyLong())).thenReturn(Optional.of(admin));

        String viewName = adminController.updateAdmin(1L, admin);

        assertEquals("redirect:/admin/list", viewName);
        verify(adminRepository, times(1)).save(admin);
    }

    @Test
    public void testUpdateAdmin_Failure() {
        when(adminRepository.findById(anyLong())).thenReturn(Optional.empty());

        try {
            adminController.updateAdmin(1L, new Admin());
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid admin ID: 1", e.getMessage());
        }
    }
    @Test
    public void testViewProfile() {
        when(session.getAttribute("username")).thenReturn("admin");
        when(session.getAttribute("password")).thenReturn("password123");
        when(session.getAttribute("email")).thenReturn("admin@example.com");
        when(session.getAttribute("phonenumber")).thenReturn("1234567890");

        ModelAndView mav = adminController.viewProfile(session);

        assertEquals("/admin/profile.html", mav.getViewName());
        assertEquals("admin", mav.getModel().get("username"));
        assertEquals("password123", mav.getModel().get("password"));
        assertEquals("admin@example.com", mav.getModel().get("email"));
        assertEquals("1234567890", mav.getModel().get("phonenumber"));
    }

    @Test
    public void testEditAdmin() {
        when(session.getAttribute("username")).thenReturn("admin");
        when(session.getAttribute("email")).thenReturn("admin@example.com");
        when(session.getAttribute("phonenumber")).thenReturn("1234567890");
        when(session.getAttribute("password")).thenReturn("password123");

        Admin admin = new Admin();
        when(adminRepository.findByUsername("admin")).thenReturn(admin);

        ModelAndView mav = adminController.editAdmin(session);

        assertEquals("/admin/edit.html", mav.getViewName());
        assertEquals("admin", mav.getModel().get("username"));
        assertEquals("admin@example.com", mav.getModel().get("email"));
        assertEquals("1234567890", mav.getModel().get("phonenumber"));
        assertEquals("password123", mav.getModel().get("password"));
        assertEquals(admin, mav.getModel().get("admin"));
    }

    @Test
    public void testEditProfile_Success() {
        when(session.getAttribute("username")).thenReturn("admin");
        Admin admin = new Admin();
        when(adminRepository.findByUsername("admin")).thenReturn(admin);

        RedirectView view = adminController.editProfile(new Admin(), "newPassword", session);

        assertEquals("/admin/Profile", view.getUrl());
        verify(adminRepository, times(1)).save(admin);
    }
    @Test
    public void testEditProfile_Failure() {
        when(session.getAttribute("username")).thenReturn("admin");
        when(adminRepository.findByUsername("admin")).thenReturn(null);

        RedirectView view = adminController.editProfile(new Admin(), "newPassword", session);

        assertEquals("/admin/Profile", view.getUrl());
        verify(adminRepository, times(0)).save(any(Admin.class));
    }

    @Test
    public void testDeleteAccount_Success() {
        when(session.getAttribute("username")).thenReturn("admin");
        Admin admin = new Admin();
        when(adminRepository.findByUsername("admin")).thenReturn(admin);

        String viewName = adminController.deleteAccount(session, model);

        assertEquals("redirect:/admin/login", viewName);
        verify(adminRepository, times(1)).delete(admin);
        verify(session, times(1)).invalidate();
    }

    @Test
    public void testDeleteAccount_AdminNotFound() {
        when(session.getAttribute("username")).thenReturn("admin");
        when(adminRepository.findByUsername("admin")).thenReturn(null);

        String viewName = adminController.deleteAccount(session, model);

        assertEquals("redirect:/admin/login", viewName);
        verify(adminRepository, times(0)).delete(any(Admin.class));
        verify(session, times(0)).invalidate();
        verify(model, times(1)).addAttribute("error", "Admin not found.");
    }

    @Test
    public void testDeleteAccount_NoAdminLoggedIn() {
        when(session.getAttribute("username")).thenReturn(null);

        String viewName = adminController.deleteAccount(session, model);

        assertEquals("redirect:/admin/login", viewName);
        verify(adminRepository, times(0)).delete(any(Admin.class));
        verify(session, times(0)).invalidate();
        verify(model, times(1)).addAttribute("error", "No admin logged in.");
    }

    @Test
    public void testListDoctors() {
        List<Doctor> doctors = new ArrayList<>();
        when(doctorRepository.findAll()).thenReturn(doctors);

        String viewName = adminController.listDoctors(model);

        assertEquals("admin/doctorList", viewName);
        verify(model, times(1)).addAttribute("Doctor", doctors);
    }

    @Test
    public void testShowAddForm() {
        String viewName = adminController.showAddForm(model);

        assertEquals("Admin/addDoctor", viewName);
        verify(model, times(1)).addAttribute("Doctor", new Doctor());
    }

    @Test
    public void testAddDoctor() {
        Doctor doctor = new Doctor();
        String encodedPassword = BCrypt.hashpw(doctor.getPassword(), BCrypt.gensalt(12));
        doctor.setPassword(encodedPassword);
        when(doctorRepository.save(any(Doctor.class))).thenReturn(doctor);

        String viewName = adminController.addDoctor(doctor);

        assertEquals("redirect:/admin/doctorList", viewName);
        verify(doctorRepository, times(1)).save(doctor);
    }

    @Test
    public void testShowEditDoctorForm_Success() {
        Doctor doctor = new Doctor();
        when(doctorRepository.findById(anyLong())).thenReturn(Optional.of(doctor));

        String viewName = adminController.showEditDoctorForm(1L, model);

        assertEquals("admin/doctorEdit", viewName);
        verify(model, times(1)).addAttribute("Doctor", doctor);
    }

    @Test
    public void testShowEditDoctorForm_Failure() {
        when(doctorRepository.findById(anyLong())).thenReturn(Optional.empty());

        try {
            adminController.showEditDoctorForm(1L, model);
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid admin ID: 1", e.getMessage());
        }
    }

    @Test
    public void testUpdateDoctor_Success() {
        Doctor doctor = new Doctor();
        when(doctorRepository.findById(anyLong())).thenReturn(Optional.of(doctor));

        String viewName = adminController.updateDoctor(1L, doctor);

        assertEquals("redirect:/admin/doctorList", viewName);
        verify(doctorRepository, times(1)).save(doctor);
    }

    @Test
    public void testUpdateDoctor_Failure() {
        when(doctorRepository.findById(anyLong())).thenReturn(Optional.empty());

        try {
            adminController.updateDoctor(1L, new Doctor());
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid doctor ID: 1", e.getMessage());
        }
    }

    @Test
    public void testDeleteDoctor_Success() {
        Doctor doctor = new Doctor();
        when(doctorRepository.findById(anyLong())).thenReturn(Optional.of(doctor));

        String viewName = adminController.deleteDoctor(1L);

        assertEquals("redirect:/admin/doctorList", viewName);
        verify(doctorRepository, times(1)).delete(doctor);
    }

    @Test
    public void testDeleteDoctor_Failure() {
        when(doctorRepository.findById(anyLong())).thenReturn(Optional.empty());

        try {
            adminController.deleteDoctor(1L);
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid doctor ID: 1", e.getMessage());
        }
    }

    @Test
    public void testGetProfile() {
        ModelAndView mav = adminController.getProfile();

        assertEquals("/admin/pets.html", mav.getViewName());
    }

    @Test
    public void testLogout() {
        String viewName = adminController.logout(session);

        assertEquals("redirect:/admin/login", viewName);
        verify(session, times(1)).invalidate();
    }
}