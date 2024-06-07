package com.example.demo.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.models.Doctor;

@Repository

public interface DoctorRepository extends JpaRepository<Doctor, Long> {


    Doctor findByName(String name);

    Doctor findByEmail(String email);



}
