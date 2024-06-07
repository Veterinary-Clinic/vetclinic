package com.example.demo.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(Long Id); 
    User findByemail(String email);
    Object findByEmail(String email);
     User save(User user);
     User findByName(String name);
    //void deleteByUsername(String username);
}

