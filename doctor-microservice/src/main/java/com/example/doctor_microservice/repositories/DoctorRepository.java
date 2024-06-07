package com.example.doctor_microservice.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.doctor_microservice.models.Doctor;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor,Long>{

    Doctor findByEmail(String email);

    Doctor findByName(String name);


    
}
