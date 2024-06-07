package com.example.demo.models;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String type;
    private String breed;
    private int age;
    private int weight;
    private String gender;


    public Pet() {
    }

    public Pet(int id, String name, String type, String breed, int age, int weight, String gender) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.breed = breed;
        this.age = age;
        this.weight = weight;
        this.gender = gender;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBreed() {
        return this.breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public int getAge() {
        return this.age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getWeight() {
        return this.weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getGender() {
        return this.gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Pet id(int id) {
        setId(id);
        return this;
    }

    public Pet name(String name) {
        setName(name);
        return this;
    }

    public Pet type(String type) {
        setType(type);
        return this;
    }

    public Pet breed(String breed) {
        setBreed(breed);
        return this;
    }

    public Pet age(int age) {
        setAge(age);
        return this;
    }

    public Pet weight(int weight) {
        setWeight(weight);
        return this;
    }

    public Pet gender(String gender) {
        setGender(gender);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Pet)) {
            return false;
        }
        Pet pet = (Pet) o;
        return id == pet.id && Objects.equals(name, pet.name) && Objects.equals(type, pet.type) && Objects.equals(breed, pet.breed) && age == pet.age && weight == pet.weight && Objects.equals(gender, pet.gender);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, type, breed, age, weight, gender);
    }

    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", name='" + getName() + "'" +
            ", type='" + getType() + "'" +
            ", breed='" + getBreed() + "'" +
            ", age='" + getAge() + "'" +
            ", weight='" + getWeight() + "'" +
            ", gender='" + getGender() + "'" +
            "}";
    }
    
  
}
