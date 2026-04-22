package com.example.profilesystem.profile.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.profilesystem.profile.entity.Profile;
import com.example.profilesystem.profile.repository.ProfileRepository;

@Service
public class ProfileService {

    private final ProfileRepository repository;
    private final BCryptPasswordEncoder encoder;

    public ProfileService(ProfileRepository repository,
            BCryptPasswordEncoder encoder) {

        this.repository = repository;
        this.encoder = encoder;
    }

    public Optional<Profile> findByToken(String token) {

        return repository.findByToken(token);
    }

    public Profile create(Profile profile) {

        profile.setPassword(
                encoder.encode(profile.getPassword()));

        return repository.save(profile);
    }

    public Profile update(Profile profile) {

        return repository.save(profile);
    }

    public boolean checkPassword(String rawPassword,
            String storedPassword) {

        return encoder.matches(rawPassword, storedPassword);
    }

    public String encodePassword(String password) {

        return encoder.encode(password);
    }

    public void deleteByToken(String token) {

        Optional<Profile> profile = repository.findByToken(token);

        if (profile.isPresent()) {

            if (profile.get().getPhotoUrl() != null) {

                Path imagePath = Paths.get(
                        System.getProperty("user.dir") +
                                profile.get().getPhotoUrl());

                try {
                    Files.deleteIfExists(imagePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            repository.delete(profile.get());
        }
    }
}