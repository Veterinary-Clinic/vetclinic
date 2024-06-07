package com.example.demo.repositories;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.models.Booking;


public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findTop10ByOrderByDateDesc();

    List<Booking> findByDateAfterOrDateEqualsOrderByDateDesc(LocalDate now, LocalDate now2);

    Booking findByUserId(Long user_id);

    Booking findByDoctorId(Long doctor_id);

    Boolean existsByUserId(Long user_id);

    Boolean existsByDoctorId(Long doctor_id);

    // List<Booking> findByDateGreaterThanEqualAndTimeGreaterThanEqualOrderByDateAscTimeAsc(LocalDate today,
    //         LocalTime currentTime);

    
    
}
