package com.example.profilesystem.profile.mapper;

import org.springframework.stereotype.Component;

import com.example.profilesystem.profile.entity.Profile;
import com.example.profilesystem.profile.entity.ProfileForm;
import com.example.profilesystem.profile.entity.ProfileFormEdit;

@Component
public class ProfileMapper {

    public Profile formToProfile(ProfileForm form) {
        return Profile.builder()
                .name(form.getName())
                .birthDate(form.getBirthDate())
                .phone(form.getPhone())
                .email(form.getEmail())
                .address(form.getAddress())
                .bloodType(form.getBloodType())
                .allergies(form.getAllergies())
                .chronicDiseases(form.getChronicDiseases())
                .medications(form.getMedications())
                .insuranceProvider(form.getInsuranceProvider())
                .insuranceNumber(form.getInsuranceNumber())
                .emergencyContactName(form.getEmergencyContactName())
                .emergencyContactPhone(form.getEmergencyContactPhone())
                .emergencyContactRelation(form.getEmergencyContactRelation())
                .password(form.getPassword())
                .build();
    }

    public void updateProfileFromForm(ProfileFormEdit profileFormEdit, Profile profile) {

        profile.setName(profileFormEdit.getName());
        profile.setBirthDate(profileFormEdit.getBirthDate());
        profile.setPhone(profileFormEdit.getPhone());
        profile.setEmail(profileFormEdit.getEmail());
        profile.setAddress(profileFormEdit.getAddress());
        profile.setBloodType(profileFormEdit.getBloodType());
        profile.setAllergies(profileFormEdit.getAllergies());
        profile.setChronicDiseases(profileFormEdit.getChronicDiseases());
        profile.setMedications(profileFormEdit.getMedications());
        profile.setInsuranceProvider(profileFormEdit.getInsuranceProvider());
        profile.setInsuranceNumber(profileFormEdit.getInsuranceNumber());
        profile.setEmergencyContactName(profileFormEdit.getEmergencyContactName());
        profile.setEmergencyContactPhone(profileFormEdit.getEmergencyContactPhone());
        profile.setEmergencyContactRelation(profileFormEdit.getEmergencyContactRelation());
    }

}
