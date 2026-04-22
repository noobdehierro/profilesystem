package com.example.profilesystem.qr.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.profilesystem.profile.service.ProfileService;
import com.example.profilesystem.qr.entity.QRCode;
import com.example.profilesystem.qr.repository.QRCodeRepository;
import com.example.profilesystem.qr.service.BulkQRService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/admin/qrs")
public class QRController {
    private final BulkQRService bulkQRService;
    private final QRCodeRepository qrRepository;
    private final ProfileService profileService;

    public QRController(QRCodeRepository qrRepository, BulkQRService bulkQRService, ProfileService profileService) {
        this.bulkQRService = bulkQRService;
        this.qrRepository = qrRepository;
        this.profileService = profileService;
    }

    @GetMapping
    public String list(Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<QRCode> result = qrRepository.findAll(PageRequest.of(page, size, Sort.by("id").descending()));
        model.addAttribute("qrs", result.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", result.getTotalPages());
        model.addAttribute("totalElements", result.getTotalElements());

        return "admin/qrList";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable String id) {

        profileService.deleteByToken(id); // 🔥 borra perfil + imagen

        qrRepository.deleteById(id);

        try {
            Files.deleteIfExists(Paths.get("qrs/qr-" + id + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "redirect:/admin/qrs";
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> download(@PathVariable String id)
            throws IOException {
        Path filePath = Paths.get("qrs/qr-" + id + ".png");
        Resource resource = new UrlResource(filePath.toUri());
        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=" + resource.getFilename())
                .body(resource);
    }

    @PostMapping("/generate") // quita el /{cantidad} del path, viene por @RequestParam
    public String generate(@RequestParam int cantidad) throws Exception {
        bulkQRService.generateBulkQR(cantidad);
        return "redirect:/admin/qrs";
    }

    @PostMapping("/download-zip")
    public ResponseEntity<byte[]> downloadZip(
            @RequestParam List<String> ids) throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (ZipOutputStream zip = new ZipOutputStream(baos)) {
            for (String id : ids) {
                Path filePath = Paths.get("qrs/qr-" + id + ".png");
                if (Files.exists(filePath)) {
                    zip.putNextEntry(new ZipEntry("qr-" + id + ".png"));
                    Files.copy(filePath, zip);
                    zip.closeEntry();
                }
            }
        }
        byte[] zipBytes = baos.toByteArray();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=qrs.zip")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(zipBytes.length)
                .body(zipBytes);
    }
}
