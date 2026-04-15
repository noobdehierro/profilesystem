package com.example.profilesystem.profile.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String token;

    private String name;

    private String photoUrl;

    private String birthDate;

    private String bloodType;

    private String phone;

    private String email;

    private String address;

    private String emergencyContactName;

    private String emergencyContactPhone;

    private String emergencyContactRelation;

    private String allergies;

    private String chronicDiseases;

    private String medications;

    private String insuranceProvider;

    private String insuranceNumber;

    private String password;

}
