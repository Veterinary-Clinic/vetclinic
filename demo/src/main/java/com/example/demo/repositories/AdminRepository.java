package com.example.demo.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.models.Admin;

@Repository
public interface AdminRepository extends JpaRepository<Admin,Integer>{

    Optional<Admin> findById(Long id);

    Admin findByusername(String name);

    

    Admin findByUsername(String name);

    // Admin findBypassword(String password);

    
}

