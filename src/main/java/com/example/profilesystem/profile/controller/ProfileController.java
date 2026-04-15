package com.example.profilesystem.profile.controller;

import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.profilesystem.profile.entity.Profile;
import com.example.profilesystem.profile.service.ProfileService;
import com.example.profilesystem.qr.service.BulkQRService;

@Controller
@RequestMapping("/profile")
public class ProfileController {
    private final ProfileService service;
    private final BulkQRService bulkQRService;
    private final BCryptPasswordEncoder encoder;

    public ProfileController(ProfileService service, BulkQRService bulkQRService, BCryptPasswordEncoder encoder) {
        this.service = service;
        this.bulkQRService = bulkQRService;
        this.encoder = encoder;
    }

    @GetMapping("/{token}")
    public String showProfile(@PathVariable String token, Model model) {

        boolean exists = bulkQRService.validateAndRegisterScan(token);

        if (!exists) {
            model.addAttribute("token", token);
            return "qrNotFound";
        }

        Optional<Profile> profile = service.findByToken(token);

        if (profile.isEmpty()) {

            model.addAttribute("token", token);
            return "createProfile";
        }

        model.addAttribute("profile", profile.get());

        return "showProfile";
    }

    @PostMapping("/{token}")
    public String createProfile(
            @PathVariable String token,
            @ModelAttribute Profile profile,
            @RequestParam String confirmPassword,
            Model model) {

        if (!profile.getPassword().equals(confirmPassword)) {
            model.addAttribute("token", token);
            model.addAttribute("error", "Las contraseñas no coinciden");

            // Mapea cada campo individualmente para que el HTML los lea
            model.addAttribute("name", profile.getName());
            model.addAttribute("birthDate", profile.getBirthDate());
            model.addAttribute("phone", profile.getPhone());
            model.addAttribute("email", profile.getEmail());
            model.addAttribute("address", profile.getAddress());
            model.addAttribute("bloodType", profile.getBloodType());
            model.addAttribute("allergies", profile.getAllergies());
            model.addAttribute("chronicDiseases", profile.getChronicDiseases());
            model.addAttribute("medications", profile.getMedications());
            model.addAttribute("insuranceProvider", profile.getInsuranceProvider());
            model.addAttribute("insuranceNumber", profile.getInsuranceNumber());
            model.addAttribute("emergencyContactName", profile.getEmergencyContactName());
            model.addAttribute("emergencyContactPhone", profile.getEmergencyContactPhone());
            model.addAttribute("emergencyContactRelation", profile.getEmergencyContactRelation());

            return "createProfile";
        }

        profile.setToken(token);
        service.create(profile);
        return "redirect:/profile/" + token;
    }

    @PostMapping("/{token}/verify")
    public String verifyPassword(@PathVariable String token,
            @RequestParam String password,
            Model model) {

        Profile profile = service
                .findByToken(token)
                .orElseThrow();

        boolean valid = service.checkPassword(
                password,
                profile.getPassword());

        if (!valid) {

            model.addAttribute("profile", profile);
            model.addAttribute("error", "Password incorrecto");

            return "showProfile";
        }

        model.addAttribute("profile", profile);

        return "editProfile";
    }

    @PostMapping("/{token}/update")
    public String updateProfile(
            @PathVariable String token,
            @RequestParam String password,
            @RequestParam(required = false) String newPassword,
            @RequestParam(required = false) String confirmNewPassword,
            @ModelAttribute Profile profileData,
            Model model) {

        Profile existingProfile = service
                .findByToken(token)
                .orElseThrow();

        if (!service.checkPassword(password, existingProfile.getPassword())) {
            // Conserva lo que el usuario escribió, no lo de la BD
            profileData.setToken(token);
            profileData.setPassword(existingProfile.getPassword()); // no exponer el hash
            model.addAttribute("profile", profileData);
            model.addAttribute("error", "Contraseña actual incorrecta");
            return "editProfile";
        }

        if (newPassword != null && !newPassword.isBlank()) {
            if (!newPassword.equals(confirmNewPassword)) {
                profileData.setToken(token);
                profileData.setPassword(existingProfile.getPassword());
                model.addAttribute("profile", profileData);
                model.addAttribute("error", "Las nuevas contraseñas no coinciden");
                return "editProfile";
            }
            existingProfile.setPassword(encoder.encode(newPassword));
        }

        existingProfile.setName(profileData.getName());
        existingProfile.setBirthDate(profileData.getBirthDate());
        existingProfile.setPhone(profileData.getPhone());
        existingProfile.setEmail(profileData.getEmail());
        existingProfile.setBloodType(profileData.getBloodType());
        existingProfile.setAllergies(profileData.getAllergies());
        existingProfile.setChronicDiseases(profileData.getChronicDiseases());
        existingProfile.setMedications(profileData.getMedications());
        existingProfile.setInsuranceProvider(profileData.getInsuranceProvider());
        existingProfile.setInsuranceNumber(profileData.getInsuranceNumber());
        existingProfile.setEmergencyContactName(profileData.getEmergencyContactName());
        existingProfile.setEmergencyContactPhone(profileData.getEmergencyContactPhone());
        existingProfile.setEmergencyContactRelation(profileData.getEmergencyContactRelation());

        service.update(existingProfile);
        return "redirect:/profile/" + token;
    }
}