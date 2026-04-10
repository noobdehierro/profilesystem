package com.example.profilesystem.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.profilesystem.entity.Profile;
import com.example.profilesystem.service.ProfileService;

@Controller
@RequestMapping("/profile")
public class ProfileController {
    private final ProfileService service;

    public ProfileController(ProfileService service) {
        this.service = service;
    }

    @GetMapping("/{token}")
    public String showProfile(@PathVariable String token, Model model) {

        Optional<Profile> profile = service.findByToken(token);

        if (profile.isEmpty()) {
            model.addAttribute("token", token);
            return "createProfile";
        }

        model.addAttribute("profile", profile.get());

        return "showProfile";
    }

    @PostMapping("/{token}")
    public String createProfile(@PathVariable String token,
            @ModelAttribute Profile profile) {

        profile.setToken(token);

        service.save(profile);

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
    public String updateProfile(@PathVariable String token,
            @ModelAttribute Profile profileData) {

        Profile profile = service
                .findByToken(token)
                .orElseThrow();

        profile.setName(profileData.getName());
        profile.setEmail(profileData.getEmail());
        profile.setPhone(profileData.getPhone());

        service.save(profile);

        return "redirect:/profile/" + token;
    }
}
