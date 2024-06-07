package com.example.demo2.services;

import com.example.demo2.models.Admin;
import com.example.demo2.models.Doctor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {
    private final RestTemplate restTemplate;
    private final String baseUrl = "http://localhost:8083"; // Base URL for the REST API

    public AdminService() {
        this.restTemplate = new RestTemplate(); // Initialize a new RestTemplate instance
    }

    public List<Admin> findAll() {
        String url = baseUrl + "/admin/list";
        return this.restTemplate.exchange(
                url, // Endpoint URL
                HttpMethod.GET, // HTTP request method
                null, // Request body
                new ParameterizedTypeReference<List<Admin>>() {
                }).getBody(); // Response body converted to List<Post>
    }

    public void save(Admin admin) {
        String url = baseUrl + "/admin/addAdmin"; // The endpoint URL for saving a post
        this.restTemplate.postForObject(url, admin, Admin.class);
    }

    public void saveDoctor(Doctor doctor) {
        String url = baseUrl + "/admin/addDoctor"; // The endpoint URL for saving a doctor
        this.restTemplate.postForObject(url, doctor, String.class);
    }

    public Optional<Admin> findById(Long id) {
        String url = baseUrl + "/admin/" + id; // Corrected endpoint URL
        Admin admin = this.restTemplate.exchange(
                url, // Endpoint URL
                HttpMethod.GET, // HTTP request method
                null, // Request body
                new ParameterizedTypeReference<Admin>() {
                }).getBody(); // Response body converted to Admin
        return Optional.ofNullable(admin);
    }

    public void delete(Admin admin) {
        String url = baseUrl + "/admin/deleteAdmin"; // The endpoint URL for deleting an admin
        this.restTemplate.postForObject(url, admin, Admin.class);
    }

    public Admin findByUsername(String username) {
        String url = baseUrl + "/admin/findByUsername/" + username; // Endpoint URL for finding by username
        return this.restTemplate.getForObject(url, Admin.class);
    }
}
