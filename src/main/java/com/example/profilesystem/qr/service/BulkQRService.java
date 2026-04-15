package com.example.profilesystem.qr.service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.profilesystem.qr.entity.QRCode;
import com.example.profilesystem.qr.repository.QRCodeRepository;
import com.example.profilesystem.qr.util.IdGenerator;

@Service
public class BulkQRService {

    private final QRGeneratorService qrGeneratorService;
    private final QRCodeRepository qrCodeRepository;
    @Value("${app.base-url}")
    private String baseUrl;

    public BulkQRService(
            QRGeneratorService qrGeneratorService,
            QRCodeRepository qrCodeRepository) {
        this.qrGeneratorService = qrGeneratorService;
        this.qrCodeRepository = qrCodeRepository;
    }

    public void generateBulkQR(int cantidad) throws Exception {

        for (int i = 0; i < cantidad; i++) {

            String id;

            do {
                id = IdGenerator.generateShortId();
            } while (qrCodeRepository.existsById(id));

            String url = baseUrl + "/profile/" + id;

            qrGeneratorService.generateQR(
                    url,
                    "qrs/qr-" + id + ".png");

            QRCode qrCode = new QRCode(id, url);

            qrCodeRepository.save(qrCode);
        }
    }

    public void updateScanCount(String id) {
        QRCode qrCode = qrCodeRepository.findById(id).orElseThrow();
        qrCode.setScanCount(qrCode.getScanCount() + 1);
        qrCode.setLastScanAt(LocalDateTime.now());

        qrCodeRepository.save(qrCode);
    }

    public boolean validateAndRegisterScan(String token) {

    Optional<QRCode> qrOptional = qrCodeRepository.findById(token);

    if (qrOptional.isEmpty()) {
        return false;
    }

    QRCode qr = qrOptional.get();

    if (qr.getExpiresAt() != null &&
        qr.getExpiresAt().isBefore(LocalDateTime.now())) {

        return false;
    }

    qr.setScanCount(qr.getScanCount() + 1);
    qr.setLastScanAt(LocalDateTime.now());

    qrCodeRepository.save(qr);

    return true;
}
}