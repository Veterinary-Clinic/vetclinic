package com.example.demo;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.example.demo.controllers.UserController;
import com.example.demo.models.Pet;
import com.example.demo.models.User;
import com.example.demo.repositories.PetRepository;
import com.example.demo.repositories.UserRepository;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PetRepository petRepository;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private Model model;

    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        session = new MockHttpSession();
    }

    @Test
    void testGetPets() {
        ModelAndView mav = userController.getpets();
        assertEquals("/user/pets.html", mav.getViewName());
        assertTrue(mav.getModel().containsKey("pets"));
    }

    @Test
    void testViewProfile() {
        session.setAttribute("id", 1L);
        session.setAttribute("name", "John Doe");
        session.setAttribute("phonenumber", "1234567890");
        session.setAttribute("email", "john.doe@example.com");

        ModelAndView mav = userController.viewProfile(session);

        assertEquals("/user/profile.html", mav.getViewName());
        assertEquals("John Doe", mav.getModel().get("name"));
        assertEquals("1234567890", mav.getModel().get("phonenumber"));
        assertEquals("john.doe@example.com", mav.getModel().get("email"));
        assertEquals(1L, mav.getModel().get("id"));
    }

    @Test
    void testAddPet() {
        ModelAndView mav = userController.addPet();
        assertEquals("/user/addPet.html", mav.getViewName());
        assertTrue(mav.getModel().containsKey("pet"));
    }

    @Test
    void testSavePet() {
        Pet pet = new Pet();
        RedirectView rv = userController.savePet(pet);
        verify(petRepository, times(1)).save(pet);
        assertEquals("pets", rv.getUrl());
    }

    @Test
    void testShowEditPetForm() {
        Pet pet = new Pet();
        when(petRepository.findById(anyLong())).thenReturn(Optional.of(pet));
        String view = userController.showEditPetForm(1L, model);
        verify(model, times(1)).addAttribute("pet", pet);
        assertEquals("user/edit", view);
    }

    @Test
    void testUpdatePet() {
        Pet pet = new Pet();
        when(petRepository.findById(anyLong())).thenReturn(Optional.of(pet));
        when(bindingResult.hasErrors()).thenReturn(false);

        String view = userController.updatePet(1L, pet, bindingResult);
        verify(petRepository, times(1)).save(pet);
        assertEquals("redirect:/user/pets", view);
    }

    @Test
    void testDeletePet() {
        Pet pet = new Pet();
        when(petRepository.findById(anyLong())).thenReturn(Optional.of(pet));
        String view = userController.deletePet(1L);
        verify(petRepository, times(1)).delete(pet);
        assertEquals("redirect:/user/pets", view);
    }

    @Test
    void testSaveUser_Registration() {
        User user = new User();
        user.setPassword("password123");
        user.setConfirmPassword("password123");
        user.setEmail("test@example.com");

        when(userRepository.findByemail("test@example.com")).thenReturn(null);
        when(bindingResult.hasErrors()).thenReturn(false);

        RedirectView rv = userController.saveUser(user, bindingResult, null);
        verify(userRepository, times(1)).save(any(User.class));
        assertEquals("/user/Registration", rv.getUrl());
    }

    @Test
    void testLoginProcess() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword(BCrypt.hashpw("password", BCrypt.gensalt()));

        when(userRepository.findByemail("test@example.com")).thenReturn(user);

        RedirectView rv = userController.loginProcess("test@example.com", "password", session);
        assertEquals("/user/index", rv.getUrl());
        assertEquals("test@example.com", session.getAttribute("email"));
    }

    @Test
    void testEditProfile() {
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");

        session.setAttribute("name", "John Doe");
        when(userRepository.findByName("John Doe")).thenReturn(user);

        ModelAndView mav = userController.editDoctor(session);

        assertEquals("user/editProfile.html", mav.getViewName());
        assertEquals("John Doe", mav.getModel().get("name"));
        assertEquals("john.doe@example.com", mav.getModel().get("email"));
        assertTrue(mav.getModel().containsKey("user"));
    }
}
