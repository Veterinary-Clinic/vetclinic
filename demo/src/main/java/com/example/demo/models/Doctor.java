package com.example.demo.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Objects;

@Entity
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String phonenumber;
    private String name;
    private String email;
    private String password;
    private String gender;




    public Doctor() {
    }

    public Doctor(Long id, String phonenumber, String name, String email, String password, String gender) {
        this.id = id;
        this.phonenumber = phonenumber;
        this.name = name;
        this.email = email;
        this.password = password;
        this.gender = gender;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhonenumber() {
        return this.phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return this.gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Doctor id(Long id) {
        setId(id);
        return this;
    }

    public Doctor phonenumber(String phonenumber) {
        setPhonenumber(phonenumber);
        return this;
    }

    public Doctor name(String name) {
        setName(name);
        return this;
    }

    public Doctor email(String email) {
        setEmail(email);
        return this;
    }

    public Doctor password(String password) {
        setPassword(password);
        return this;
    }

    public Doctor gender(String gender) {
        setGender(gender);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Doctor)) {
            return false;
        }
        Doctor doctor = (Doctor) o;
        return Objects.equals(id, doctor.id) && Objects.equals(phonenumber, doctor.phonenumber) && Objects.equals(name, doctor.name) && Objects.equals(email, doctor.email) && Objects.equals(password, doctor.password) && Objects.equals(gender, doctor.gender);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, phonenumber, name, email, password, gender);
    }

    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", phonenumber='" + getPhonenumber() + "'" +
            ", name='" + getName() + "'" +
            ", email='" + getEmail() + "'" +
            ", password='" + getPassword() + "'" +
            ", gender='" + getGender() + "'" +
            "}";
    }



}
