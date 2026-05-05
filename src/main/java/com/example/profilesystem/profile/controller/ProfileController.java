package com.example.profilesystem.profile.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.profilesystem.profile.entity.Profile;
import com.example.profilesystem.profile.entity.ProfileForm;
import com.example.profilesystem.profile.entity.ProfileFormEdit;
import com.example.profilesystem.profile.service.ProfileService;
import com.example.profilesystem.qr.service.BulkQRService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/profile")
public class ProfileController {
    private final ProfileService profileService;
    private final BulkQRService bulkQRService;

    public ProfileController(ProfileService profileService, BulkQRService bulkQRService) {
        this.profileService = profileService;
        this.bulkQRService = bulkQRService;
        ;
    }

    @GetMapping("/{token_id}")
    public String showProfile(@PathVariable String token_id, Model model) {

        boolean exists = bulkQRService.validateAndRegisterScan(token_id);

        if (!exists) {
            model.addAttribute("token_id", token_id);
            return "qrNotFound";
        }

        Optional<Profile> profile = profileService.findByToken(token_id);

        if (profile.isEmpty()) {

            model.addAttribute("token_id", token_id);
            model.addAttribute("profile", new ProfileForm());
            return "createProfile";
        }

        model.addAttribute("profile", profile.get());

        return "showProfile";
    }

    @PostMapping("/{token_id}")
    public String createProfile(
            @PathVariable String token_id,
            @Valid @ModelAttribute("profile") ProfileForm profileForm,
            BindingResult bindingResult,
            Model model) {

        if (profileForm.getPhoto() == null || profileForm.getPhoto().isEmpty()) {
            bindingResult.rejectValue(
                    "photo",
                    "photo.empty",
                    "La imagen es obligatoria");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("token_id", token_id);
            return "createProfile";
        }

        if (!profileForm.passwordMatch()) {
            bindingResult.rejectValue(
                    "confirmPassword",
                    "password.mismatch",
                    "Las contraseñas no coinciden");
            return "createProfile";
        }

        profileService.createProfile(token_id, profileForm);
        return "redirect:/profile/" + token_id;
    }

    @PostMapping("/{token}/verify")
    public String verifyPassword(@PathVariable String token,
            @RequestParam String password,
            Model model) {

        Profile profile = profileService
                .findByToken(token)
                .orElseThrow();

        boolean valid = profileService.checkPassword(
                password,
                profile.getPassword());

        if (!valid) {
            model.addAttribute("profile", profile);

            model.addAttribute("error", "Password incorrecto");

            return "showProfile";

        }

        ProfileFormEdit formEdit = new ProfileFormEdit();
        formEdit.setToken(profile.getToken());
        formEdit.setName(profile.getName());
        formEdit.setBirthDate(profile.getBirthDate());
        formEdit.setPhone(profile.getPhone());
        formEdit.setEmail(profile.getEmail());
        formEdit.setAddress(profile.getAddress());
        formEdit.setBloodType(profile.getBloodType());
        formEdit.setAllergies(profile.getAllergies());
        formEdit.setChronicDiseases(profile.getChronicDiseases());
        formEdit.setMedications(profile.getMedications());
        formEdit.setInsuranceProvider(profile.getInsuranceProvider());
        formEdit.setInsuranceNumber(profile.getInsuranceNumber());
        formEdit.setEmergencyContactName(profile.getEmergencyContactName());
        formEdit.setEmergencyContactPhone(profile.getEmergencyContactPhone());
        formEdit.setEmergencyContactRelation(profile.getEmergencyContactRelation());

        model.addAttribute("profile", formEdit);
        model.addAttribute("photoUrl", profile.getPhotoUrl());

        return "editProfile";
    }

    @PostMapping("/{token_id}/update")
    public String updateProfile(@PathVariable String token_id,
            @Valid @ModelAttribute("profile") ProfileFormEdit profileFormEdit,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("profile", profileFormEdit);
            return "editProfile";
        }

        if (profileFormEdit.getPhoto() == null || profileFormEdit.getPhoto().isEmpty()) {
            bindingResult.rejectValue(
                    "photo",
                    "photo.empty",
                    "La imagen es obligatoria");
        }
        if (profileFormEdit.getNewPassword() != null && !profileFormEdit.getNewPassword().isBlank()) {
            if (!profileFormEdit.passwordMatch()) {
                bindingResult.rejectValue(
                        "confirmPassword",
                        "password.mismatch",
                        "Las contraseñas no coinciden");
                return "editProfile";
            }
        }
        Profile profile = profileService
                .findByToken(token_id)
                .orElseThrow();

        boolean valid = profileService.checkPassword(
                profileFormEdit.getPassword(),
                profile.getPassword());
        if (!valid) {
            bindingResult.rejectValue(
                    "password",
                    "password.incorrect",
                    "Password incorrecto");
            return "editProfile";
        }

        profileService.updateProfile(token_id, profileFormEdit);

        return "redirect:/profile/" + token_id;
    }
}