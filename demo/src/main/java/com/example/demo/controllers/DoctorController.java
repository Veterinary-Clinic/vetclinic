package com.example.demo.controllers;

import com.example.demo.models.Doctor;
import com.example.demo.models.Pet;
import com.example.demo.repositories.DoctorRepository;
import com.example.demo.repositories.PetRepository;

import jakarta.servlet.http.HttpSession;

import java.util.List;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/doctor")
public class DoctorController {
    @Autowired
    public DoctorRepository doctorRepository;

    
    @Autowired
    private PetRepository petRepository;

    @GetMapping("login")
    public ModelAndView login() {
        ModelAndView mav = new ModelAndView("/doctors/loginDoctor.html");
        mav.addObject("doctor", new Doctor());
        return mav;
    }

    @PostMapping("login")
    public RedirectView loginProgress(@RequestParam("email") String email,
            @RequestParam("password") String password, HttpSession session) {
        Doctor dbDoctor = this.doctorRepository.findByEmail(email);
        if (dbDoctor != null && BCrypt.checkpw(password, dbDoctor.getPassword())) {
            session.setAttribute("name", dbDoctor.getName());
            session.setAttribute("email", dbDoctor.getEmail());
            session.setAttribute("phonenumber", dbDoctor.getPhonenumber());
            session.setAttribute("doctor_id", dbDoctor.getId()); // Add id to session attributes
            return new RedirectView("Profile"); // Redirect to the appropriate page
        } else {
            return new RedirectView("login"); // Redirect to login page with error flag
        }
    }

    @GetMapping("index")
    public ModelAndView gethome() {
        ModelAndView mav = new ModelAndView("/doctors/index.html");
        return mav;
    }

   

    @GetMapping("Profile")
    public ModelAndView viewProfile(HttpSession session) {
        ModelAndView mav = new ModelAndView("/doctors/ProfileDoctor.html"); // Retrieve attributes from session or

        // doctor object
        Long id = (Long) session.getAttribute("doctor_id"); // Retrieve doctor's ID from session
        String name = (String) session.getAttribute("name");
        String email = (String) session.getAttribute("email"); // Retrieve email from session or doctor object
        String phonenumber = (String) session.getAttribute("phonenumber"); // Retrieve phonenumber from doctor object
        String password = (String) session.getAttribute("password");
        // Add attributes to the ModelAndView
        mav.addObject("name", name);
        mav.addObject("email", email);
        mav.addObject("password", password);
        mav.addObject("phonenumber", phonenumber);
        mav.addObject("id", id); // Add doctor's ID to the ModelAndView
        return mav;
    }

    @GetMapping("/editProfile")
    public ModelAndView editDoctor(HttpSession session) {
        ModelAndView mav = new ModelAndView("doctors/editProfile.html");
        String name = (String) session.getAttribute("name");
        String email = (String) session.getAttribute("email");
        String phonenumber = (String) session.getAttribute("phonenumber");
        String password = (String) session.getAttribute("password");
        mav.addObject("name", name);
        mav.addObject("email", email);
        mav.addObject("phonenumber", phonenumber);
        mav.addObject("password", password);
        Doctor dr = doctorRepository.findByName(name);
        mav.addObject("doctor", dr);
        return mav;
    }

    @PostMapping("/editProfile")
    public RedirectView editProfile(@ModelAttribute Doctor updatedDoctor,
            @RequestParam(value = "newPassword", required = false) String newPassword, HttpSession session) {
        String name = (String) session.getAttribute("name");
        Doctor existingUser = doctorRepository.findByName(name);

        if (existingUser != null) {
            existingUser.setName(updatedDoctor.getName());
            existingUser.setEmail(updatedDoctor.getEmail());

            // Check if a new password is provided and update it
            if (newPassword != null && !newPassword.isEmpty()) {
                // Don't hash the password if you want to store it as plain text
                existingUser.setPassword(newPassword);
            }

            doctorRepository.save(existingUser);

            // Update session attribute if necessary
            session.setAttribute("name", existingUser.getName());
            session.setAttribute("email", existingUser.getEmail());
            session.setAttribute("password", existingUser.getPassword());
        }

        return new RedirectView("/doctor/Profile");
    }

    @GetMapping("all-doctors")
    public ModelAndView getDoctors() {
        ModelAndView mav = new ModelAndView("/user/doctors.html");
        List<Doctor> doctors = doctorRepository.findAll();
        mav.addObject("doctors", doctors);
        return mav;
    }

    @GetMapping("/logout")
    public RedirectView logout(HttpSession session) {
        // Invalidate the session
        session.invalidate();
        return new RedirectView("/doctor/login");
    }

    @GetMapping("/pets")
    public ModelAndView getpets() {
        ModelAndView mav = new ModelAndView("/doctors/pets.html");
        List<Pet> pets = this.petRepository.findAll();
        mav.addObject("pets", pets);
        return mav;
    }
}