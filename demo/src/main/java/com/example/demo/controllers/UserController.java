package com.example.demo.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.example.demo.models.Pet;
import com.example.demo.models.User;
import com.example.demo.repositories.DoctorRepository;
import com.example.demo.repositories.PetRepository;
import com.example.demo.repositories.UserRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")

public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PetRepository petRepository;

    @GetMapping("/pets")
    public ModelAndView getpets() {
        ModelAndView mav = new ModelAndView("/user/pets.html");
        List<Pet> pets = this.petRepository.findAll();
        mav.addObject("pets", pets);
        return mav;
    }

    @GetMapping("profile")
    public ModelAndView viewProfile(HttpSession session) {
        ModelAndView mav = new ModelAndView("/user/profile.html");
        Long id = (Long) session.getAttribute("id");
        String name = (String) session.getAttribute("name");
        String phonenumber = (String) session.getAttribute("phonenumber"); 
        String email = (String) session.getAttribute("email");

        // Add attributes to the ModelAndView
        mav.addObject("name", name);
        mav.addObject("email", email);
        mav.addObject("phonenumber", phonenumber);
        mav.addObject("id", id);
        return mav;
    }

    @GetMapping("addPet")
    public ModelAndView addPet() {
        ModelAndView mav = new ModelAndView("/user/addPet.html");
        mav.addObject("pet", new Pet());
        return mav;
    }

    @PostMapping("addPet")
    public RedirectView savePet(@ModelAttribute Pet npet) {
        petRepository.save(npet);
        return new RedirectView("pets");
    }

    @GetMapping("/{id}/editPet")
    public String showEditPetForm(@PathVariable("id") Long id, Model model) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid pet ID: " + id));
        model.addAttribute("pet", pet);
        return "user/edit"; // Assuming "user/edit" is your edit pet form template
    }
    
    @PostMapping("/{id}/editPet")
    public String updatePet(@Valid @PathVariable("id") Long id, @ModelAttribute("pet") Pet updatedPet,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "user/edit"; // Return the edit form if there are validation errors
        }
        
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Pet ID: " + id));
        pet.setName(updatedPet.getName()); // Update the name
        pet.setBreed(updatedPet.getBreed()); // Update the breed
        pet.setAge(updatedPet.getAge()); // Update the age
        pet.setWeight(updatedPet.getWeight()); // Update the weight
        
        petRepository.save(pet);
        return "redirect:/user/pets"; // Redirect to the pets page after successful update
    }
    




    @PostMapping("/{id}/deletePet")
    public String deletePet(@PathVariable("id") Long id) {

        Pet npet = petRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid pet ID: " + id));
        petRepository.delete(npet);
        return "redirect:/user/pets";
    }

    @GetMapping("/index")
    public ModelAndView getHomePage() {
        ModelAndView mav = new ModelAndView("/user/index.html");
        List<User> users = this.userRepository.findAll();
        mav.addObject("users", users);
        return mav;
    }

    @GetMapping("Registration")
    public ModelAndView addUser() {
        ModelAndView mav = new ModelAndView("/user/Signup.html");
        User newUser = new User();
        mav.addObject("user", newUser);
        return mav;
    }

    @PostMapping("Registration")
    public RedirectView saveUser(@Valid @ModelAttribute User user, BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        // Check if the password meets the minimum length requirement
        String password = user.getPassword();
        if (password == null || password.length() < 8) {
            bindingResult.addError(new FieldError("user", "password", "Password must be at least 8 characters long."));
            return new RedirectView("/user/Registration");
        }

        // Check if the email has not been used before
        if (userRepository.findByemail(user.getEmail()) != null) {
            bindingResult.addError(new FieldError("user", "email", "Email is already in use."));
            return new RedirectView("/user/Registration");
        }

        // Check if the password matches the confirm password
        String confirmPassword = user.getConfirmPassword();
        if (!password.equals(confirmPassword)) {
            bindingResult.addError(new FieldError("user", "confirmPassword", "Passwords do not match."));
            return new RedirectView("/user/Registration");
        }

        // Hash the password and save the user
        String encodedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));
        user.setPassword(encodedPassword);

        // Saving user details
        userRepository.save(user);

        return new RedirectView("/user/Registration");
    }

    @GetMapping("/check-email")
    @ResponseBody
    public Map<String, Boolean> checkEmailAvailability(@RequestParam("email") String email) {
        boolean exists = (userRepository.findByEmail(email) != null);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return response;
    }

    @GetMapping("Login")
    public ModelAndView login() {
        ModelAndView mav = new ModelAndView("/user/Signup.html");
        User newUser = new User();
        mav.addObject("user", newUser);
        return mav;
    }

    @PostMapping("Login")
    public RedirectView loginProcess(@RequestParam("email") String email,
            @RequestParam("password") String password, HttpSession session) {
        User dbUser = this.userRepository.findByemail(email);
        if (dbUser != null && BCrypt.checkpw(password, dbUser.getPassword())) {
            session.setAttribute("email", dbUser.getEmail());
            session.setAttribute("user_id", dbUser.getId());
            session.setAttribute("name", dbUser.getName());
            session.setAttribute("phonenumber", dbUser.getPhone());
            return new RedirectView("/user/index");
        } else {
            return new RedirectView("/user/Registration");
        }
    }

    // @PostMapping("/editUser/{id}")
    // public RedirectView updateMovie(@PathVariable("id") Long id, @ModelAttribute
    // User updateUser) {
    // User existingUser = userRepository.findById(id).orElse(null);
    // if (existingUser != null) {
    // existingUser.setName(updateUser.getName());
    // existingUser.setEmail(updateUser.getEmail());
    // existingUser.setPhone(updateUser.getPhone());
    // userRepository.save(existingUser);
    // }
    // return new RedirectView("/user/profile");
    // }
    @GetMapping("/{id}/editUser")
    public String showEditAdminForm(@PathVariable("id") Long id, Model model) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid admin ID: " + id));
        model.addAttribute("user", user);
        return "user/edit";
    }

    @PostMapping("/{id}/editUser")
    public String updateAdmin(@Valid @PathVariable("id") Long id, @ModelAttribute("User") User updatedUser,
            BindingResult bindingResult) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid admin ID: " + id));
        user.setName(updatedUser.getName());
        if (bindingResult.hasErrors()) {
            return "redirect:/user/edit";
        } else {
            userRepository.save(user);
            return "redirect:/user/profile";
        }
    }
    @GetMapping("/editProfile")
    public ModelAndView editDoctor(HttpSession session) {
        ModelAndView mav = new ModelAndView("user/editProfile.html");
        String name = (String) session.getAttribute("name");
        String email = (String) session.getAttribute("email");
        String phonenumber = (String) session.getAttribute("phonenumber");
        String password = (String) session.getAttribute("password");
        mav.addObject("name", name);
        mav.addObject("email", email);
        mav.addObject("phonenumber", phonenumber);
        mav.addObject("password", password);
        User ur = userRepository.findByName(name);
        mav.addObject("user", ur);
        return mav;
    }

    @PostMapping("/editProfile")
    public RedirectView editProfile(@ModelAttribute User updateUser,
            @RequestParam(value = "newPassword", required = false) String newPassword, HttpSession session) {
        String name = (String) session.getAttribute("name");
        User existingUser = userRepository.findByName(name);

        if (existingUser != null) {
            existingUser.setName(updateUser.getName());
            existingUser.setEmail(updateUser.getEmail());

            // Check if a new password is provided and update it
            if (newPassword != null && !newPassword.isEmpty()) {
                // Don't hash the password if you want to store it as plain text
                existingUser.setPassword(newPassword);
            }

            userRepository.save(existingUser);

            // Update session attribute if necessary
            session.setAttribute("name", existingUser.getName());
            session.setAttribute("email", existingUser.getEmail());
            session.setAttribute("password", existingUser.getPassword());
        }

        return new RedirectView("/user/profile");
    }
    @GetMapping("/logout")
    public RedirectView logout(HttpSession session) {
        // Invalidate the session
        session.invalidate();
        return new RedirectView("/user/index");
    }
}