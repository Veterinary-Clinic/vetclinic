package com.example.demo.repositories;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.models.Pet;

@Repository
public interface PetRepository extends JpaRepository<Pet, Integer> {

    void deleteById(Long id);

    Optional<Pet> findById(Long id);
} 
