package com.example.profilesystem.profile.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String token;

    // Datos personales
    private String name;
    private String photoUrl;
    private String birthDate;
    private String phone;
    private String email;
    private String address;

    // Información médica
    private String bloodType;
    private String allergies;
    private String chronicDiseases;
    private String medications;

    // Seguro médico
    private String insuranceProvider;
    private String insuranceNumber;

    // Contacto de emergencia
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String emergencyContactRelation;

    // Seguridad
    private String password;
}
