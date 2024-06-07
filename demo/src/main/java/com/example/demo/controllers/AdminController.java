package com.example.demo.controllers;

import com.example.demo.models.Admin;
import com.example.demo.models.Appointment;
import com.example.demo.models.Booking;
import com.example.demo.models.Doctor;
import com.example.demo.models.Pet;
import com.example.demo.repositories.AdminRepository;
import com.example.demo.repositories.AppointmentRepository;
import com.example.demo.repositories.BookingRepository;
import com.example.demo.repositories.DoctorRepository;
import com.example.demo.repositories.UserRepository;

import jakarta.servlet.http.HttpSession;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private BookingRepository bookingrepository;

    @GetMapping("/index") 
    public String indexAdmins(Model model) {
        long patientCount = userRepository.count();

        List<Appointment> appointments = appointmentRepository.findTop10ByOrderByDateDesc();
        List<Booking> bookings = bookingrepository.findByDateAfterOrDateEqualsOrderByDateDesc(LocalDate.now(),
                LocalDate.now());

        if (bookings.isEmpty()) {
            bookings = new ArrayList<>();
        }
        model.addAttribute("patientCount", patientCount);
        model.addAttribute("appointments", appointments);
        model.addAttribute("bookings", bookings);

        return "admin/index";
    }

    @GetMapping("/countPatients")
    public String countPatients(Model model) {
        long patientCount = userRepository.count();
        model.addAttribute("patientCount", patientCount);
        return "admin/index";
    }

    @GetMapping("/countAppointments")
    public String countAppointments(Model model) {
        List<Booking> bookings = bookingrepository.findTop10ByOrderByDateDesc();
        model.addAttribute("bookings", bookings);
        return "admin/index";
    }

    @GetMapping("/list")
    public String listAdmins(Model model) {
        model.addAttribute("admins", adminRepository.findAll());
        return "admin/list";
    }

    @GetMapping("/addAdmin")
    public String showAddAdminForm(Model model) {
        model.addAttribute("admin", new Admin());
        return "admin/addAdmin";
    }

    @PostMapping("/addAdmin")
    public String addAdmin(@Validated @ModelAttribute("admin") Admin admin, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "admin/addAdmin";
        }

        else {
            String encodedPassword = BCrypt.hashpw(admin.getPassword(), BCrypt.gensalt(12));
            admin.setPassword(encodedPassword);
            adminRepository.save(admin);
            return "redirect:/admin/list";
        }
    }

    @GetMapping("login")
    public ModelAndView login() {
        ModelAndView mav = new ModelAndView("/admin/loginAdmin.html");
        mav.addObject("admin", new Admin());
        return mav;
    }

    @PostMapping("login")
    public RedirectView loginProgress(@RequestParam("username") String name,
            @RequestParam("password") String password ,@RequestParam("id") String id, HttpSession session) {

        Admin dbAdmin = this.adminRepository.findByusername(name);
        if (dbAdmin != null && BCrypt.checkpw(password, dbAdmin.getPassword())) {
            session.setAttribute("id", dbAdmin.getId()); // Set admin ID in the session
            session.setAttribute("username", dbAdmin.getUsername());
            session.setAttribute("password", dbAdmin.getPassword());
            session.setAttribute("gmail", dbAdmin.getEmail());
            session.setAttribute("phone", dbAdmin.getPhonenumber());
            return new RedirectView("index");
        } else {
            return new RedirectView("login");
        }
    }

    // @GetMapping("/profile")
    // public ModelAndView viewProfile(HttpSession session) {
    //     String name = (String) session.getAttribute("username");
    //     String password = (String) session.getAttribute("password");
    //     String email = (String) session.getAttribute("gmail");
    //     String phonenumber = (String) session.getAttribute("phone");
    
    //     Admin admin = new Admin();
    //     admin.setUsername(name);
    //     admin.setPassword(password); // Make sure to set password
    //     admin.setEmail(email);
    //     admin.setPhonenumber(phonenumber);
    
    //     ModelAndView mav = new ModelAndView("/admin/profileAdmin.html");
    //     mav.addObject("Admin", admin); // Add the Admin object to the model
    
    //     return mav;
    // }
    

    @GetMapping("/{id}/editAdmin")
    public String showEditAdminForm(@PathVariable("id") Long id, Model model) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid admin ID: " + id));
        model.addAttribute("admin", admin);
        return "admin/editAdmins";
    }

    @PostMapping("/{id}/editAdmin")
    public String updateAdmin(@PathVariable("id") Long id, @ModelAttribute("admin") Admin updatedAdmin) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid admin ID: " + id));
        admin.setUsername(updatedAdmin.getUsername());
        admin.setEmail(updatedAdmin.getEmail());
        admin.setPhonenumber(updatedAdmin.getPhonenumber());
    
        adminRepository.save(admin);
        return "redirect:/admin/list";
    }
    
    // @GetMapping("/{id}/editAdmin")
    // public String showEditAdminForm(@PathVariable("id") Long id, Model model) {
    //     Admin admin = adminRepository.findById(id)
    //             .orElseThrow(() -> new IllegalArgumentException("Invalid admin ID: " + id));
    //     model.addAttribute("admin", admin);
    //     return "admin/edit"; 
    // }

    // @PostMapping("/{id}/editAdmin")
    // public String updateAdmin(@Valid @PathVariable("id") Long id, @ModelAttribute("admin") Admin updatedAdmin,
    //         BindingResult bindingResult) {
    //     if (bindingResult.hasErrors()) {
    //         return "admin/edit"; 
    //     }

    //     Admin admin = adminRepository.findById(id)
    //             .orElseThrow(() -> new IllegalArgumentException("Invalid admin ID: " + id));
    //     admin.setUsername(updatedAdmin.getUsername());
    //     admin.setEmail(updatedAdmin.getEmail());
    //     admin.setPhonenumber(updatedAdmin.getPhonenumber());

    //     adminRepository.save(admin);
    //     return "redirect:/admin/profile"; 
    // }

    @GetMapping("Profile")
    public ModelAndView viewProfile(HttpSession session) {
        // Admin admin = new Admin();
        ModelAndView mav = new ModelAndView("/admin/profile.html");
        
        String username = (String) session.getAttribute("username");
        String password = (String) session.getAttribute("password");
        String email = (String) session.getAttribute("email");
        String phonenumber = (String) session.getAttribute("phonenumber");
      
        mav.addObject("username", username);
        mav.addObject("password", password);
        mav.addObject("phonenumber", phonenumber);
        mav.addObject("email", email);
        
        return mav;
    }

    @GetMapping("/edit")
    public ModelAndView editAdmin(HttpSession session) {
        ModelAndView mav = new ModelAndView("/admin/edit.html");
        String username = (String) session.getAttribute("username");
        String email = (String) session.getAttribute("email");
        String phonenumber = (String) session.getAttribute("phonenumber");
        String password = (String) session.getAttribute("password");

        mav.addObject("username", username);
        mav.addObject("email", email);
        mav.addObject("phonenumber", phonenumber);
        mav.addObject("password", password);

        Admin admin = adminRepository.findByUsername(username);
        mav.addObject("admin", admin);
        return mav;
    }

 
    @PostMapping("/edit")
    public RedirectView editProfile(@ModelAttribute Admin updatedadmin,
            @RequestParam(value = "newPassword", required = false) String newPassword, HttpSession session) {
        String username = (String) session.getAttribute("username");
        Admin existingAdmin = adminRepository.findByUsername(username);

        if (existingAdmin != null) {
            existingAdmin.setUsername(updatedadmin.getUsername());
            existingAdmin.setEmail(updatedadmin.getEmail());
            existingAdmin.setPhonenumber(updatedadmin.getPhonenumber());
            // Check if a new password is provided and update it
            if (newPassword != null && !newPassword.isEmpty()) {
                
                existingAdmin.setPassword(newPassword);
            }

            adminRepository.save(existingAdmin);

            // Update session attribute if necessary
            session.setAttribute("username", existingAdmin.getUsername());
            session.setAttribute("email", existingAdmin.getEmail());
            session.setAttribute("phonenumber", existingAdmin.getPhonenumber());
            session.setAttribute("password", existingAdmin.getPassword());
        }

        return new RedirectView("/admin/Profile");
    }

    // in the profile
    @PostMapping("/deleteAccount")
    public String deleteAccount(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");

        if (username != null) {
            Admin admin = adminRepository.findByUsername(username);
            if (admin != null) {
                adminRepository.delete(admin);
                session.invalidate();
            } else {
                model.addAttribute("error", "Admin not found.");
            }
        } else {
            model.addAttribute("error", "No admin logged in.");
        }

        return "redirect:/admin/login";
    

}

    @PostMapping("/{id}/deleteAdmin")
    public String deleteAdmin(@PathVariable("id") Long id) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid doctor ID: " + id));
        adminRepository.delete(admin);
        return "redirect:/admin/list";
    }

    // doctor
    @GetMapping("/doctorList")
    public String listDoctors(Model model) {
        model.addAttribute("Doctor", doctorRepository.findAll());
        return "admin/doctorList";
    }

    @GetMapping("/addDoctor")
    public String showAddForm(Model model) {
        model.addAttribute("Doctor", new Doctor());
        return "Admin/addDoctor";
    }

    @PostMapping("/addDoctor")
    public String addDoctor(@ModelAttribute("Doctor") Doctor doctor) {
        String encodedPassword = BCrypt.hashpw(doctor.getPassword(), BCrypt.gensalt(12));
        doctor.setPassword(encodedPassword);
        this.doctorRepository.save(doctor);
        return "redirect:/admin/doctorList";
    }

    // @PostMapping("/addDoctor")
    // public String addDoctor(@ModelAttribute("Doctor") Doctor doctor) {
    // .orElseThrow(() -> new IllegalArgumentException("Invalid doctor ID: " + id));
    // model.addAttribute("Doctor", Doctor);
    // return "admin/doctorEdit";
    // }
    @GetMapping("/{id}/editDoctor")
    public String showEditDoctorForm(@PathVariable("id") Long id, Model model) {
        Doctor Doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid admin ID: " + id));
        model.addAttribute("Doctor", Doctor);
        return "admin/doctorEdit";
    }

    @PostMapping("/{id}/editDoctor")
    public String updateDoctor(@PathVariable("id") Long id, @ModelAttribute("Doctor") Doctor updatedDoctor) {
        Doctor Doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid doctor ID: " + id));
        Doctor.setName(updatedDoctor.getName());

        doctorRepository.save(Doctor);
        return "redirect:/admin/doctorList";
    }

    @PostMapping("/{id}/deleteDoctor")
    public String deleteDoctor(@PathVariable("id") Long id) {

        Doctor Doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid doctor ID: " + id));
        doctorRepository.delete(Doctor);
        return "redirect:/admin/doctorList";
    }
    // @ExceptionHandler
    // public String handleValidationException(Exception exception) {
    // return "admin/validation-error";
    // }

    @GetMapping("/pets")
    public ModelAndView getProfile() {
        ModelAndView mav = new ModelAndView("/admin/pets.html");
        return mav;
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin/login";
    }
}
