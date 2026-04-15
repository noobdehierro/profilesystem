package com.example.profilesystem.qr.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.profilesystem.qr.entity.QRCode;

public interface QRCodeRepository extends JpaRepository<QRCode, String> {

}
