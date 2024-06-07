package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.models.Appointment;
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

    List<Appointment> findTop10ByOrderByDateDesc();

    List<Appointment> findByDoctorId(long doctorId);

    // List<Appointment> findByDateGreaterThanEqualAndTimeGreaterThanEqualOrderByDateAscTimeAsc(LocalDate today,
    //         String currentTimeString);
    
}
 