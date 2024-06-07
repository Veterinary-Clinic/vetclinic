package com.example.admin_microservice.controllers;

import com.example.admin_microservice.dto.DoctorDTO;
import com.example.admin_microservice.models.Admin;
import com.example.admin_microservice.repositories.AdminRepository;

import aj.org.objectweb.asm.TypeReference;
import jakarta.servlet.http.HttpSession;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
// import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
@RestController
@RequestMapping("/admin")
public class AdminController {
 @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private RestTemplate restTemplate;

    private final String doctorMicroserviceBaseUrl = "http://localhost:8082";

    ObjectMapper objectMapper = new ObjectMapper();

@PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        Admin dbAdmin = adminRepository.findByUsername(username);
        if (dbAdmin != null && BCrypt.checkpw(password, dbAdmin.getPassword())) {
            return new ResponseEntity<>(dbAdmin, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/findByUsername/{username}")
    public ResponseEntity<Admin> findByUsername(@PathVariable String username) {
        Admin admin = adminRepository.findByUsername(username);
        return admin != null ? new ResponseEntity<>(admin, HttpStatus.OK)
                             : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
@GetMapping("/Profile")
    public ModelAndView viewProfile(HttpSession session) {
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

    @PostMapping("/edit")
    public ResponseEntity<?> editProfile(@RequestBody Map<String, Object> body, HttpSession session) {
        String username = (String) session.getAttribute("username");
        Admin existingAdmin = adminRepository.findByUsername(username);

        if (existingAdmin != null) {
            existingAdmin.setUsername((String) body.get("username"));
            existingAdmin.setEmail((String) body.get("email"));
            existingAdmin.setPhonenumber((String) body.get("phonenumber"));

            String newPassword = (String) body.get("newPassword");
            if (newPassword != null && !newPassword.isEmpty()) {
                existingAdmin.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt(12)));
            }

            adminRepository.save(existingAdmin);

            session.setAttribute("username", existingAdmin.getUsername());
            session.setAttribute("email", existingAdmin.getEmail());
            session.setAttribute("phonenumber", existingAdmin.getPhonenumber());
            session.setAttribute("password", existingAdmin.getPassword());

            return new ResponseEntity<>(existingAdmin, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Admin not found", HttpStatus.NOT_FOUND);
        }
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
    // @PostMapping("/addDoctor")
    // public ResponseEntity<?> addDoctor(@RequestBody Map<String, String> body) {
    //     ResponseEntity<?> response = restTemplate.postForEntity(doctorMicroserviceBaseUrl + "/doctor/addDoctor", body, String.class);
    //     if (response.getStatusCode() == HttpStatus.CREATED) {
    //         return new ResponseEntity<>(response.getBody(), HttpStatus.CREATED);
    //     } else {
    //         return new ResponseEntity<>("Failed to add doctor", HttpStatus.INTERNAL_SERVER_ERROR);
    //     }
    // }
//     @PostMapping("/addDoctor")
// public ResponseEntity<?> addDoctor(@RequestBody Map<String, String> body) {
//     try {
//         // Send the doctor data to the admin microservice to save
//         Admin admin = new Admin();
//         admin.setEmail(body.get("email"));
//         admin.setUsername(body.get("username"));
//         admin.setPassword(body.get("password"));
//         admin.setPhonenumber(body.get("phonenumber"));
//         admin.setGender(body.get("gender"));
//         ResponseEntity<Admin> response = restTemplate.postForEntity(doctorMicroserviceBaseUrl + "/admin/doctorList", admin, Admin.class);
        
//         if (response.getStatusCode() == HttpStatus.CREATED) {
//             return new ResponseEntity<>(response.getBody(), HttpStatus.CREATED);
//         } else {
//             return new ResponseEntity<>("Failed to add doctor", HttpStatus.INTERNAL_SERVER_ERROR);
//         }
//     } catch (Exception e) {
//         return new ResponseEntity<>("Failed to add doctor: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//     }
// }


 

 

    @PostMapping("/addDoctor")
    public String addDoctor(@ModelAttribute DoctorDTO doctorDTO, Model model) {
        ResponseEntity<?> response = restTemplate.postForEntity(doctorMicroserviceBaseUrl + "/doctors", doctorDTO, String.class);
        if (response.getStatusCode() == HttpStatus.CREATED) {
            model.addAttribute("successMessage", "Doctor added successfully!");
        } else {
            model.addAttribute("errorMessage", "Failed to add doctor!");
        }
        return "redirect:/admin/doctorList";
    }

    @GetMapping("/doctorList")
    public String listDoctors(Model model) throws JsonMappingException, JsonProcessingException {
        ResponseEntity<String> response = restTemplate.getForEntity(doctorMicroserviceBaseUrl + "/doctors", String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            // Parse the response body into a list of DoctorDTO objects
            java.util.List<DoctorDTO> doctors = objectMapper.readValue(response.getBody(), new com.fasterxml.jackson.core.type.TypeReference<java.util.List<DoctorDTO>>() {});
          
            model.addAttribute("doctors", doctors);
        } else {
            model.addAttribute("errorMessage", "Failed to retrieve doctors!");
        }
        return "admin/doctorList";
    }

    // Other methods for managing admins in the Admin microservice...

    @GetMapping("/list")
    public ResponseEntity<List<Admin>> listAdmins() {
        List<Admin> admins = adminRepository.findAll();
        return new ResponseEntity<>(admins, HttpStatus.OK);
    }

    @PostMapping("/addAdmin")
    public ResponseEntity<Admin> addAdmin(@RequestBody Map<String, String> body) {
        Admin admin = new Admin();
        admin.setEmail(body.get("email"));
        admin.setUsername(body.get("username"));
        admin.setPassword(body.get("password"));
        admin.setPhonenumber(body.get("phonenumber"));
        admin.setGender(body.get("gender"));
        this.adminRepository.save(admin);
        return new ResponseEntity<>(admin, HttpStatus.CREATED);
    }

    @PostMapping("/{id}/editAdmin")
    public ResponseEntity<Admin> updateAdmin(@PathVariable("id") Long id, @ModelAttribute("admin") Admin updatedAdmin) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid admin ID: " + id));
        admin.setUsername(updatedAdmin.getUsername());
        admin.setEmail(updatedAdmin.getEmail());
        admin.setPhonenumber(updatedAdmin.getPhonenumber());
        adminRepository.save(admin);
        return new ResponseEntity<>(admin, HttpStatus.OK);
    }

    @PostMapping("/{id}/deleteAdmin")
    public ResponseEntity<Admin> deleteAdmin(@PathVariable("id") Long id) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid admin ID: " + id));
        adminRepository.delete(admin);
        return new ResponseEntity<>(admin, HttpStatus.OK);
    }
}
