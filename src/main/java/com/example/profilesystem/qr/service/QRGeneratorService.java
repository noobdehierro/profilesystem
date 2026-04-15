package com.example.profilesystem.qr.service;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

@Service
public class QRGeneratorService {

    public void generateQR(String text, String filePath) throws Exception {
        File directory = new File("qrs");

        if (!directory.exists()) {
            directory.mkdirs();
        }

        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(text, BarcodeFormat.QR_CODE, 300, 300);

        BufferedImage image = new BufferedImage(
                300,
                300,
                BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < 300; x++) {
            for (int y = 0; y < 300; y++) {

                image.setRGB(
                        x,
                        y,
                        matrix.get(x, y) ? 0x000000 : 0xFFFFFF);
            }
        }

        ImageIO.write(image, "png", new File(filePath));
    }
}