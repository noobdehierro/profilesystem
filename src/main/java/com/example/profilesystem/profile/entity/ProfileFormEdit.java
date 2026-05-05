package com.example.profilesystem.profile.entity;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileFormEdit {
    // Identificación
    private String token;

    // Datos personales
    @NotBlank(message = "El nombre es obligatorio")
    private String name;
    private MultipartFile photo;
    @NotBlank(message = "La fecha de nacimiento es obligatoria")
    private String birthDate;
    @NotBlank(message = "El teléfono es obligatorio")
    private String phone;
    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El correo electrónico no es válido")
    private String email;
    @NotBlank(message = "La dirección es obligatoria")
    private String address;

    // Información médica
    @NotBlank(message = "El tipo de sangre es obligatorio")
    private String bloodType;
    private String allergies = "Ninguna";
    private String chronicDiseases = "Ninguna";
    private String medications = "Ninguna";

    // Seguro médico
    @NotBlank(message = "El proveedor de seguro es obligatorio")
    private String insuranceProvider;
    @NotBlank(message = "El número de seguro es obligatorio")
    private String insuranceNumber;

    // Contacto de emergencia
    @NotBlank(message = "El nombre del contacto de emergencia es obligatorio")
    private String emergencyContactName;
    @NotBlank(message = "El teléfono del contacto de emergencia es obligatorio")
    private String emergencyContactPhone;
    @NotBlank(message = "La relación con el contacto de emergencia es obligatoria")
    private String emergencyContactRelation;

    // Seguridad
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    private String newPassword;
    private String confirmNewPassword;

    public boolean passwordMatch() {
        return newPassword != null && newPassword.equals(confirmNewPassword);
    }
}
