package com.nailsaas.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.nailsaas.enums.ErrorCodeEnum;
import com.nailsaas.exception.BusinessException;

@Service
public class NailPostImageStorageService {

    private static final DateTimeFormatter DIRECTORY_DATE = DateTimeFormatter.BASIC_ISO_DATE;
    private static final DateTimeFormatter FILE_TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    private final Path storageRoot;

    public NailPostImageStorageService(
            @Value("${nail-post.image.storage-root:D:/Nail/postImage}") String storageRoot) {
        this.storageRoot = Path.of(storageRoot).toAbsolutePath().normalize();
    }

    public StoredImage store(MultipartFile file, String manicuristCode) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCodeEnum.REQUEST_INVALID);
        }
        String originalFilename = file.getOriginalFilename();
        String extension = extensionOf(originalFilename);
        String safeCode = manicuristCode == null ? "" : manicuristCode.replaceAll("[^A-Za-z0-9_-]", "_");
        if (safeCode.isBlank()) {
            throw new BusinessException(ErrorCodeEnum.REQUEST_INVALID);
        }

        LocalDate today = LocalDate.now();
        String storageName = LocalDateTime.now().format(FILE_TIMESTAMP) + "_" + UUID.randomUUID() + extension;
        Path relativePath = Path.of(safeCode, today.format(DIRECTORY_DATE), storageName);
        Path destination = storageRoot.resolve(relativePath).normalize();
        if (!destination.startsWith(storageRoot)) {
            throw new BusinessException(ErrorCodeEnum.REQUEST_INVALID);
        }

        try {
            Files.createDirectories(destination.getParent());
            file.transferTo(destination);
            String contentType = detectContentType(destination);
            if (contentType == null) {
                Files.deleteIfExists(destination);
                throw new BusinessException(ErrorCodeEnum.REQUEST_INVALID);
            }
            return new StoredImage(storageName, relativePath.toString().replace('\\', '/'),
                    originalFilename, contentType, Files.size(destination), destination);
        } catch (IOException ex) {
            throw new IllegalStateException("圖片儲存失敗", ex);
        }
    }

    public void delete(StoredImage image) {
        try {
            Files.deleteIfExists(image.absolutePath());
        } catch (IOException ex) {
            throw new IllegalStateException("圖片刪除失敗", ex);
        }
    }

    public void deleteStoredPath(String storagePath) {
        Path storedFile = storageRoot.resolve(storagePath).normalize();
        if (!storedFile.startsWith(storageRoot)) {
            throw new BusinessException(ErrorCodeEnum.REQUEST_INVALID);
        }
        try {
            Files.deleteIfExists(storedFile);
        } catch (IOException ex) {
            throw new IllegalStateException("圖片刪除失敗", ex);
        }
    }

    private String extensionOf(String filename) {
        if (filename == null) {
            return "";
        }
        String basename = filename.replace('\\', '/');
        basename = basename.substring(basename.lastIndexOf('/') + 1);
        int dot = basename.lastIndexOf('.');
        if (dot < 0 || dot == basename.length() - 1) {
            throw new BusinessException(ErrorCodeEnum.REQUEST_INVALID);
        }
        String extension = basename.substring(dot).toLowerCase(Locale.ROOT);
        if (!extension.matches("\\.[a-z0-9]{1,10}")) {
            throw new BusinessException(ErrorCodeEnum.REQUEST_INVALID);
        }
        return extension;
    }

    private String detectContentType(Path file) throws IOException {
        byte[] header = new byte[12];
        byte[] signature;
        try (InputStream input = Files.newInputStream(file)) {
            signature = input.readNBytes(header.length);
        }
        int count = signature.length;
        System.arraycopy(signature, 0, header, 0, count);
        if (count >= 3 && (header[0] & 0xff) == 0xff && (header[1] & 0xff) == 0xd8
                && (header[2] & 0xff) == 0xff) return "image/jpeg";
        if (count >= 8 && (header[0] & 0xff) == 0x89 && header[1] == 'P' && header[2] == 'N'
                && header[3] == 'G' && (header[4] & 0xff) == 0x0d && (header[5] & 0xff) == 0x0a
                && (header[6] & 0xff) == 0x1a && (header[7] & 0xff) == 0x0a) return "image/png";
        if (count >= 6 && header[0] == 'G' && header[1] == 'I' && header[2] == 'F'
                && header[3] == '8' && (header[4] == '7' || header[4] == '9') && header[5] == 'a') return "image/gif";
        if (count >= 12 && header[0] == 'R' && header[1] == 'I' && header[2] == 'F' && header[3] == 'F'
                && header[8] == 'W' && header[9] == 'E' && header[10] == 'B' && header[11] == 'P') return "image/webp";
        if (count >= 2 && header[0] == 'B' && header[1] == 'M') return "image/bmp";
        return null;
    }

    public record StoredImage(String storageName, String storagePath, String originalName,
            String contentType, long fileSize, Path absolutePath) {
    }
}
