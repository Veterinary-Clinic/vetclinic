package com.example.demo2.controllers;

import com.example.demo2.models.Admin;
import com.example.demo2.models.Doctor;
import com.example.demo2.services.DoctorService;
import jakarta.servlet.http.HttpSession;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// @RestController
@Controller
@RequestMapping("/doctor")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;
    @Autowired
    private RestTemplate restTemplate;

    private final String doctorMicroserviceBaseUrl = "http://localhost:8082";
    private final String adminMicroserviceBaseUrl= "http://localhost:8083";
    
    @GetMapping("/login")
    public ModelAndView login() {
        ModelAndView mav = new ModelAndView("/doctors/loginDoctor.html");
        mav.addObject("doctor", new Doctor());
        return mav;
    }

    @PostMapping("/login")
    public RedirectView loginProgress(@RequestParam("email") String email,
                                      @RequestParam("password") String password, HttpSession session) {
        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);

        ResponseEntity<Doctor> response = restTemplate.postForEntity(doctorMicroserviceBaseUrl + "/doctor/login", body, Doctor.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            Doctor dbDoctor = response.getBody();
            session.setAttribute("id", dbDoctor.getId());
            session.setAttribute("name", dbDoctor.getName());
            session.setAttribute("password", dbDoctor.getPassword());
            session.setAttribute("email", dbDoctor.getEmail());
            session.setAttribute("phone", dbDoctor.getPhonenumber());
            return new RedirectView("index");
        } else {
            return new RedirectView("login");
        }
    }
   @GetMapping("/adddoctor")
    public String showAddDoctorForm(Model model) {
        model.addAttribute("doctor", new Doctor());
        return "doctors/adddoctor";
    }

    @PostMapping("/adddoctor")
    public String addDoctor( @ModelAttribute("doctor") Doctor doctor, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "doctors/adddoctor"; // Return to the form in case of errors
        } else {
            String encodedPassword = BCrypt.hashpw(doctor.getPassword(), BCrypt.gensalt(12));
            doctor.setPassword(encodedPassword);
            doctorService.save(doctor);
            return "redirect:/doctor/login"; // Redirect to the login page after successful addition
        }
    }


    
    @GetMapping("index")
    public ModelAndView getHome() {
        return new ModelAndView("/doctors/index.html");
    }

    @GetMapping("Profile")
    public ModelAndView viewProfile(HttpSession session) {
        ModelAndView mav = new ModelAndView("/doctors/ProfileDoctor.html");
        Long id = (Long) session.getAttribute("doctor_id");
        String name = (String) session.getAttribute("name");
        String email = (String) session.getAttribute("email");
        String phonenumber = (String) session.getAttribute("phonenumber");

        mav.addObject("name", name);
        mav.addObject("email", email);
        mav.addObject("phonenumber", phonenumber);
        mav.addObject("id", id);
        return mav;
    }

    // @GetMapping("/editProfile")
    // public ModelAndView editDoctor(HttpSession session) {
    //     ModelAndView mav = new ModelAndView("doctors/editProfile.html");
    //     String name = (String) session.getAttribute("name");
    //     String email = (String) session.getAttribute("email");
    //     String phonenumber = (String) session.getAttribute("phonenumber");

    //     mav.addObject("name", name);
    //     mav.addObject("email", email);
    //     mav.addObject("phonenumber", phonenumber);

    //     Doctor dr = doctorService.findByName(name);
    //     mav.addObject("doctor", dr);
    //     return mav;
    // }

    // @PostMapping("/editProfile")
    // public RedirectView editProfile(@ModelAttribute Doctor updatedDoctor,
    //                                 @RequestParam(value = "newPassword", required = false) String newPassword,
    //                                 HttpSession session) {
    //     String name = (String) session.getAttribute("name");
    //     Doctor existingDoctor = doctorService.findByName(name);

    //     if (existingDoctor != null) {
    //         existingDoctor.setName(updatedDoctor.getName());
    //         existingDoctor.setEmail(updatedDoctor.getEmail());
    //         existingDoctor.setPhonenumber(updatedDoctor.getPhonenumber());

    //         if (newPassword != null && !newPassword.isEmpty()) {
    //             existingDoctor.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
    //         }

    //         doctorService.save(existingDoctor);

    //         session.setAttribute("name", existingDoctor.getName());
    //         session.setAttribute("email", existingDoctor.getEmail());
    //         session.setAttribute("phonenumber", existingDoctor.getPhonenumber());
    //     }

    //     return new RedirectView("/doctor/Profile");
    // }

    // @GetMapping("all-doctors")
    // public ModelAndView getDoctors() {
    //     ModelAndView mav = new ModelAndView("/user/doctors.html");
    //     List<Doctor> doctors = doctorService.findAll();
    //     mav.addObject("doctors", doctors);
    //     return mav;
    // }

    @GetMapping("/logout")
    public RedirectView logout(HttpSession session) {
        session.invalidate();
        return new RedirectView("/doctor/login");
    }
    
    
}
