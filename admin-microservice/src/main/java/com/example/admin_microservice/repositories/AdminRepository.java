package com.example.admin_microservice.repositories;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import com.example.admin_microservice.models.Admin;

@Repository
public interface AdminRepository extends JpaRepository<Admin,Integer>{
// private final RestTemplate restTemplate;
//     private final String baseUrl = "http://localhost:8082";
    
    Optional<Admin> findById(Long id);

    Admin findByusername(String name);

    

    Admin findByUsername(String name);

    // Admin findBypassword(String password);

    
}


