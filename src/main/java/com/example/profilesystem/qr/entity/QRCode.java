package com.example.profilesystem.qr.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QRCode {
    @Id
    private String id;

    @Column(nullable = false)
    private String url;

    private boolean used = false;

    private int scanCount = 0;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime lastScanAt;

    private LocalDateTime expiresAt;

    public QRCode(String id, String url) {
        this.id = id;
        this.url = url;
    }

}
