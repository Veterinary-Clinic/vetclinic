package com.example.demo2.services;

import com.example.demo2.models.Doctor;


import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import java.util.Optional;

@Service
public class DoctorService {
    private final RestTemplate restTemplate;
    private final String baseUrl = "http://localhost:8082"; // Base URL for the microservice API

    public DoctorService() {
        this.restTemplate = new RestTemplate(); // Initialize a new RestTemplate instance
    }

    public void save(Doctor doctor) {
        String url = baseUrl + "/doctor/addDoctor"; // Ensure this matches the actual endpoint
        try {
            this.restTemplate.postForObject(url, doctor, Doctor.class);
        } catch (Exception e) {
            // Handle exceptions appropriately, maybe log them or rethrow them
            throw new RuntimeException("Failed to save doctor: " + e.getMessage(), e);
        }
    }
    
   


    public Optional<Doctor> login(String email, String password) {
        String url = baseUrl + "/doctor/login"; // Assuming this endpoint exists
        HttpEntity<Map<String, String>> request = new HttpEntity<>(Map.of("email", email, "password", password));
        ResponseEntity<Doctor> response = this.restTemplate.exchange(
                url, // Endpoint URL
                HttpMethod.POST, // HTTP request method
                request, // Request body containing email and password
                new ParameterizedTypeReference<Doctor>() {
                });

        return Optional.ofNullable(response.getBody());
    }

    public Doctor findByEmail(String email) {
        return restTemplate.getForObject(baseUrl + "/" + email, Doctor.class);
    }
    

   
}
