package com.example.demo.models;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private LocalDate date;
    private LocalTime startHr;
    private LocalTime endHr;

    @Column(name = "doctor_id", updatable = false, insertable = false)
    private Long doctorId; 

    @ManyToOne
    private Doctor doctor;

    public Appointment() {
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

    public Doctor getDoctor() {
        return this.doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }


    // public String clockTimeConversion(int hour) {
    // if (hour == 12) {
    // return hour + ":00 PM";
    // } else if (hour > 12) {
    // return (hour - 12) + ":00 PM";
    // } else {
    // return hour + ":00 AM";
    // }
    // }

    // private void hourList(){
    // int startHour = timeSplit(this.startHr);
    // int endHour = timeSplit(this.endHr);

    // for (int hour = startHour; hour <= endHour; hour++) {
    // System.out.println(clockTimeConversion(hour));
    // //clockTimeConversion(hour);
    // }
    // }

    // private int numberOfWorkingHours(){
    // int startHour = timeSplit(this.startHr);
    // int endHour = timeSplit(this.endHr);
    // return endHour - startHour;
    // }

}