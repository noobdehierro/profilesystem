package com.example.profilesystem.profile.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.profilesystem.profile.entity.Profile;
import com.example.profilesystem.profile.entity.ProfileForm;
import com.example.profilesystem.profile.entity.ProfileFormEdit;
import com.example.profilesystem.profile.mapper.ProfileMapper;
import com.example.profilesystem.profile.repository.ProfileRepository;
import com.example.profilesystem.qr.repository.QRCodeRepository;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final BCryptPasswordEncoder encoder;
    private final QRCodeRepository qrCodeRepository;
    private final ProfileMapper profileMapper;

    public ProfileService(ProfileRepository profileRepository,
            BCryptPasswordEncoder encoder, QRCodeRepository qrCodeRepository, ProfileMapper profileMapper) {

        this.profileRepository = profileRepository;
        this.encoder = encoder;
        this.qrCodeRepository = qrCodeRepository;
        this.profileMapper = profileMapper;
    }

    public Optional<Profile> findByToken(String token) {

        return profileRepository.findByToken(token);
    }

    public boolean checkPassword(String rawPassword,
            String storedPassword) {

        return encoder.matches(rawPassword, storedPassword);
    }

    public String encodePassword(String password) {

        return encoder.encode(password);
    }

    public void deleteByToken(String token) {
        profileRepository.findByToken(token).ifPresent(profile -> {
            if (profile.getPhotoUrl() != null) {
                Path imagePath = Paths.get(
                        System.getProperty("user.dir") + profile.getPhotoUrl());
                try {
                    Files.deleteIfExists(imagePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            profileRepository.delete(profile);
        });
        qrCodeRepository.findById(token).ifPresent(qr -> {
            Path qrPath = Paths.get(System.getProperty("user.dir") + "/qrs/qr-" + token + ".png");
            try {
                Files.deleteIfExists(qrPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            qrCodeRepository.delete(qr);
        });
    }

    public void createProfile(String token, ProfileForm form) {
        String photoUrl = savePhoto(token, form.getPhoto());

        Profile profile = profileMapper.formToProfile(form);
        profile.setPhotoUrl(photoUrl);
        profile.setToken(token);
        profile.setPassword(encodePassword(form.getPassword()));

        profileRepository.save(profile);

        markQrAsUsed(token);
    }

    public void updateProfile(String token_id, ProfileFormEdit profileFormEdit) {

        String photoUrl = savePhoto(token_id, profileFormEdit.getPhoto());

        Profile profile = profileRepository
                .findByToken(token_id)
                .orElseThrow(
                        () -> new RuntimeException("El perfil no existe"));

        profileMapper.updateProfileFromForm(profileFormEdit, profile);

        if (photoUrl != null) {
            profile.setPhotoUrl(photoUrl);
        }

        if (profileFormEdit.getNewPassword() != null && !profileFormEdit.getNewPassword().isBlank()) {
            profile.setPassword(encodePassword(profileFormEdit.getNewPassword()));
        }

        profileRepository.save(profile);
    }

    private String savePhoto(String token, MultipartFile photo) {

        Profile oldProfile = profileRepository.findByToken(token).orElse(null);

        // si no subió nueva foto, conserva la actual
        if (photo == null || photo.isEmpty()) {
            return oldProfile != null ? oldProfile.getPhotoUrl() : null;
        }

        String folderPath = System.getProperty("user.dir") + "/uploads/profiles/";
        File directory = new File(folderPath);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        // borrar foto vieja
        if (oldProfile != null && oldProfile.getPhotoUrl() != null) {
            deleteOldPhoto(oldProfile.getPhotoUrl());
        }

        String filename = token + "_" + photo.getOriginalFilename();
        File destination = new File(directory, filename);

        try {
            photo.transferTo(destination);
        } catch (IOException e) {
            throw new RuntimeException("Error guardando imagen", e);
        }

        return "/uploads/profiles/" + filename;
    }

    private void deleteOldPhoto(String photoUrl) {
        Path oldPhotoPath = Paths.get(System.getProperty("user.dir") + photoUrl);
        try {
            Files.deleteIfExists(oldPhotoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void markQrAsUsed(String token) {
        qrCodeRepository.findById(token).ifPresent(qr -> {
            qr.setUsed(true);
            qrCodeRepository.save(qr);
        });
    }

}
