package com.example.profilesystem.profile.service;

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
}