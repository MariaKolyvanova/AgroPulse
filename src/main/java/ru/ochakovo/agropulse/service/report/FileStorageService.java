package ru.ochakovo.agropulse.service.report;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class FileStorageService {

    @Value("${app.reports.upload-dir:${java.io.tmpdir}/agropulse/reports}")
    private String uploadDirectory;

    public void save(MultipartFile file, String storedFilename) {

        try {
            Path uploadDir = Path.of(uploadDirectory).toAbsolutePath().normalize();
            Files.createDirectories(uploadDir);

            Path target = uploadDir.resolve(storedFilename).normalize();

            file.transferTo(target.toFile());

        } catch (IOException e) {
            throw new IllegalStateException("Не удалось сохранить файл", e);
        }
    }

    public InputStream openStream(String storedFilename) throws IOException {
        Path uploadDir = Path.of(uploadDirectory).toAbsolutePath().normalize();
        Path file = uploadDir.resolve(storedFilename).normalize();
        return Files.newInputStream(file);
    }

    public void delete(String storedFilename) {
        try {
            Path uploadDir = Path.of(uploadDirectory).toAbsolutePath().normalize();
            Path file = uploadDir.resolve(storedFilename).normalize();
            Files.deleteIfExists(file);
        } catch (IOException ignored) {}
    }
}