package com.example.demo2.controllers;

import com.example.demo2.services.AdminService;
import com.example.demo2.services.DoctorService;
// import com.example.demo2.services.UserService;
import com.example.demo2.models.Admin;
import com.example.demo2.models.User;
import com.example.demo2.models.Doctor;
// import com.example.admin_microservice.dto.DoctorDTO;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private DoctorService doctorService; 
    // @Autowired
    // private UserService userService;

    @Autowired
    private RestTemplate restTemplate;

    private final String doctorMicroserviceBaseUrl = "http://localhost:8082";
    private final String adminMicroserviceBaseUrl= "http://localhost:8083";
    
    // @GetMapping("/index") 
    // public String indexAdmins(Model model) {
    //     long patientCount = userService.count();

    //     List<Appointment> appointments = appointmentRepository.findTop10ByOrderByDateDesc();
    //     List<Booking> bookings = bookingrepository.findByDateAfterOrDateEqualsOrderByDateDesc(LocalDate.now(),
    //             LocalDate.now());

    //     if (bookings.isEmpty()) {
    //         bookings = new ArrayList<>();
    //     }
    //     model.addAttribute("patientCount", patientCount);
    //     model.addAttribute("appointments", appointments);
    //     model.addAttribute("bookings", bookings);

    //     return "admin/index";
    // }

    @GetMapping("/list")
    public ModelAndView listAdmins() {
        ModelAndView mav = new ModelAndView("/admin/list.html");
        List<Admin> admins = this.adminService.findAll();
        mav.addObject("admins", admins);
        return mav;
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
        } else {
            String encodedPassword = BCrypt.hashpw(admin.getPassword(), BCrypt.gensalt(12));
            admin.setPassword(encodedPassword);
            adminService.save(admin);
            return "redirect:/admin/list";
        }
    }

    @GetMapping("/login")
    public ModelAndView login() {
        ModelAndView mav = new ModelAndView("/admin/loginAdmin.html");
        mav.addObject("admin", new Admin());
        return mav;
    }

    @PostMapping("/login")
    public RedirectView loginProgress(@RequestParam("username") String name,
                                      @RequestParam("password") String password, HttpSession session) {
        Map<String, String> body = new HashMap<>();
        body.put("username", name);
        body.put("password", password);

        try {
            ResponseEntity<Admin> response = restTemplate.postForEntity(adminMicroserviceBaseUrl + "/admin/login", body, Admin.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                Admin dbAdmin = response.getBody();
                session.setAttribute("id", dbAdmin.getId());
                session.setAttribute("username", dbAdmin.getUsername());
                session.setAttribute("password", dbAdmin.getPassword());
                session.setAttribute("email", dbAdmin.getEmail());
                session.setAttribute("phonenumber", dbAdmin.getPhonenumber());
                return new RedirectView("Profile");
            } else {
                return new RedirectView("login");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new RedirectView("login");
        }
    }

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

        Admin admin = adminService.findByUsername(username);
        mav.addObject("admin", admin);
        return mav;
    }
    @PostMapping("/edit")
    public RedirectView editProfile(@ModelAttribute Admin updatedadmin,
            @RequestParam(value = "newPassword", required = false) String newPassword, HttpSession session) {
        String username = (String) session.getAttribute("username");
        Admin existingAdmin = adminService.findByUsername(username);

        if (existingAdmin != null) {
            existingAdmin.setUsername(updatedadmin.getUsername());
            existingAdmin.setEmail(updatedadmin.getEmail());
            existingAdmin.setPhonenumber(updatedadmin.getPhonenumber());
            // Check if a new password is provided and update it
            if (newPassword != null && !newPassword.isEmpty()) {
                
                existingAdmin.setPassword(newPassword);
            }

            adminService.save(existingAdmin);

            // Update session attribute if necessary
            session.setAttribute("username", existingAdmin.getUsername());
            session.setAttribute("email", existingAdmin.getEmail());
            session.setAttribute("phonenumber", existingAdmin.getPhonenumber());
            session.setAttribute("password", existingAdmin.getPassword());
        }

        return new RedirectView("/admin/Profile");
    }
    @PostMapping("/deleteAccount")
    public String deleteAccount(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");

        if (username != null) {
            Admin admin = adminService.findByUsername(username);
            if (admin != null) {
                adminService.delete(admin);
                session.invalidate();
            } else {
                model.addAttribute("error", "Admin not found.");
            }
        } else {
            model.addAttribute("error", "No admin logged in.");
        }

        return "redirect:/admin/login";
    

}
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

    // @GetMapping("/doctorList")
    // public ModelAndView listDoctors(Model model) {
    //     ModelAndView mav = new ModelAndView("/admin/doctorList.html");
    //     ResponseEntity<List<DoctorDTO>> response = restTemplate.exchange(
    //             doctorMicroserviceBaseUrl + "/doctors",
    //             HttpMethod.GET,
    //             null,
    //             new ParameterizedTypeReference<List<DoctorDTO>>() {}
    //     );
    //     if (response.getStatusCode() == HttpStatus.OK) {
    //         List<DoctorDTO> doctors = response.getBody();
    //         mav.addObject("doctors", doctors);
    //     } else {
    //         model.addAttribute("errorMessage", "Failed to retrieve doctors!");
    //     }
    //     return mav;
    // }

    // @GetMapping("/addDoctor")
    // public String showAddDoctorForm(Model model) {
    //     model.addAttribute("doctorDTO", new DoctorDTO());
    //     return "admin/addDoctor";
    // }

    // @PostMapping("/addDoctor")
    // public String addDoctor(@Valid @ModelAttribute("doctorDTO") DoctorDTO doctorDTO, Model model) {
    //     ResponseEntity<?> response = restTemplate.postForEntity(
    //             doctorMicroserviceBaseUrl + "/admin/addDoctor",
    //             doctorDTO,
    //             String.class
    //     );
    //     if (response.getStatusCode() == HttpStatus.CREATED) {
    //         return "redirect:/admin/doctorList";
    //     } else {
    //         model.addAttribute("errorMessage", "Failed to add doctor!");
    //         return "admin/addDoctor";
    //     }
    // }


    @GetMapping("/{id}/editAdmin")
    public String showEditAdminForm(@PathVariable("id") Long id, Model model) {
        Admin admin = adminService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid admin ID: " + id));
        model.addAttribute("Admin", admin);
        return "admin/editAdmin";
    }

    @PostMapping("/{id}/editAdmin")
    public String updateAdmin(@PathVariable("id") Long id, @ModelAttribute("Admin") Admin updatedAdmin) {
        Admin admin = adminService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid admin ID: " + id));
        admin.setUsername(updatedAdmin.getUsername());
        admin.setEmail(updatedAdmin.getEmail());
        admin.setPhonenumber(updatedAdmin.getPhonenumber());

        adminService.save(admin);
        return "redirect:/admin/list";
    }

    @PostMapping("/{id}/deleteAdmin")
    public String deleteAdmin(@PathVariable("id") Long id) {
        Admin admin = adminService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Admin ID: " + id));
        adminService.delete(admin);
        return "redirect:/admin/list";
    }
}
