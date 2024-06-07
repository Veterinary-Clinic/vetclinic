package com.example.demo.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

import java.time.LocalDate;
import java.time.LocalTime;


@Entity
public class Booking {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;
    private LocalDate date; 
    private LocalTime startHr;
    private LocalTime endHr;

    @Column(name = "doctor_id", updatable = false, insertable = false)
    private Long doctorId;

    @Column(name = "user_id", updatable = false, insertable = false)
    private Long userId; 
    
    @ManyToOne
    private Doctor doctor;

    @OneToOne
    private User user;

    public Booking() {
    }

    public Booking(int id, LocalDate date, LocalTime startHr, LocalTime endHr, Long doctorId, Long userId, Doctor doctor, User user) {
        this.id = id;
        this.date = date;
        this.startHr = startHr;
        this.endHr = endHr;
        this.doctorId = doctorId;
        this.userId = userId;
        this.doctor = doctor;
        this.user = user;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartHr() {
        return this.startHr;
    }

    public void setStartHr(LocalTime startHr) {
        this.startHr = startHr;
    }

    public LocalTime getEndHr() {
        return this.endHr;
    }

    public void setEndHr(LocalTime endHr) {
        this.endHr = endHr;
    }

    public Long getDoctorId() {
        return this.doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Doctor getDoctor() {
        return this.doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }
   

}
