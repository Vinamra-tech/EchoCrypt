package com.vinamra.encrypt_decrypt_backend.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

// Removed @Autowired, preferring constructor injection via @RequiredArgsConstructor
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.vinamra.encrypt_decrypt_backend.dto.DecryptionDTO;
import com.vinamra.encrypt_decrypt_backend.dto.EncryptionDTO;
import com.vinamra.encrypt_decrypt_backend.dto.FileLogDTO;
import com.vinamra.encrypt_decrypt_backend.service.EmailService;
import com.vinamra.encrypt_decrypt_backend.service.EncrytionService;
import com.vinamra.encrypt_decrypt_backend.service.FileLogService;
import com.vinamra.encrypt_decrypt_backend.util.QRCodeUtil;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EncryptionServiceImpl implements EncrytionService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${file.encrypted-files-dir}")
    private String encryptedDir;

    @Value("${file.decrypted-files-dir}")
    private String decryptedDir;

    private final FileLogService fileLogService;
    private final RestTemplate restTemplate;
    private final EmailService emailService;

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadDir));
            Files.createDirectories(Paths.get(encryptedDir));
            Files.createDirectories(Paths.get(decryptedDir));
            log.info("Directories created or already exist.");
        } catch (IOException e) {
            log.error("Failed to create directories", e);
            throw new RuntimeException("Failed to create directories", e);
        }
    }

    @Override
    public EncryptionDTO handleEncryption(MultipartFile multipartFile, Optional<String> userMailOptional) throws Exception {
        String originalFileName = multipartFile.getOriginalFilename();
        // --- START FIX for original_file_name NOT NULL constraint ---
        if (originalFileName == null || originalFileName.trim().isEmpty()) {
            originalFileName = "unnamed_file_" + System.currentTimeMillis(); // Provide a default name
            log.warn("MultipartFile.getOriginalFilename() returned null or empty. Using default name: {}", originalFileName);
        }
        // --- END FIX ---
        log.info("Encryption request for file: {}", originalFileName);
        log.debug("File size: {}", multipartFile.getSize());
        log.debug("Email (optional): {}", userMailOptional.orElse("N/A"));

        // Declare Path variables here, outside the try block
        Path tempOriginalFilePath = null;
        Path tempEncryptedFilePath = null;
        Path tempQrFilePath = null;
        Path zipPath = null; // This will hold the path to the final zipped file

        ZoneId zone = ZoneId.of("Asia/Kolkata");
        LocalDateTime localTime = LocalDateTime.now(zone);

        FileLogDTO fileLogDTO = null;

        try {
            // Assign values within the try block
            tempOriginalFilePath = Files.createTempFile("original_", "_" + originalFileName);
            Files.copy(multipartFile.getInputStream(), tempOriginalFilePath, StandardCopyOption.REPLACE_EXISTING);

            PythonEncryptResponse response = callFlaskEncryptAPI(tempOriginalFilePath.toFile());

            log.debug("Flask encryption response password: {}", response.getPassword());

            if (response.getExitCode() != 0 || response.getEncryptedFile() == null || response.getQrCodeFile() == null) {
                fileLogDTO = new FileLogDTO(null, originalFileName, null, null, "ENCRYPTION", localTime, "FAILURE", response.getStderr());
                fileLogService.saveLog(fileLogDTO);
                throw new RuntimeException("Encryption failed: " + response.getStderr());
            }

            String encryptedFileName = originalFileName;
            if (originalFileName != null && originalFileName.contains(".")) {
                 encryptedFileName = originalFileName.substring(0, originalFileName.lastIndexOf(".")) + "_encrypted.txt";
            } else {
                 encryptedFileName = originalFileName + "_encrypted.txt";
            }

            String qrFileName = "qr.png"; // Unique naming is good practice here as well

            tempEncryptedFilePath = Files.createTempFile("encrypted_", "_" + encryptedFileName);
            tempQrFilePath = Files.createTempFile("qr_", "_" + qrFileName);

            Files.write(tempEncryptedFilePath, response.getEncryptedFile());
            Files.write(tempQrFilePath, response.getQrCodeFile());

            if (!Files.exists(tempEncryptedFilePath) || !Files.exists(tempQrFilePath)) {
                throw new RuntimeException("Required Files missing after encryption");
            }

            String finalZipName = originalFileName + ".enc.zip";
            zipPath = Files.createTempFile("final_encrypted_", "_" + finalZipName); // Assign to the declared variable

            try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath))) {
                addToZipFile(tempEncryptedFilePath, zos);
                addToZipFile(tempQrFilePath, zos);
            } catch (IOException e) {
                log.error("Zipping of files failed", e);
                throw new RuntimeException("Zipping of files failed");
            }

            // --- Email Sending Logic (Revised) ---
            if (userMailOptional.isPresent()) {
                String email = userMailOptional.get();
                try {
                    byte[] zipFileBytes = Files.readAllBytes(zipPath);
                    emailService.sendEncryptedFileEmail(email, zipFileBytes, finalZipName , response.getPassword());
                    log.info("Email with encrypted file sent to: {}", email);
                } catch (Exception e) {
                    log.error("Failed to send encrypted file email to {}: {}", email, e.getMessage(), e);
                    // Decide if you want to update log remarks for email failure
                }
            } else {
                log.info("No email provided for backup for file: {}", originalFileName);
            }
            // --- End Email Sending Logic ---

            fileLogDTO = new FileLogDTO(null, originalFileName, encryptedFileName, null, "ENCRYPTION", localTime, "SUCCESS", response.getStdout());
            fileLogService.saveLog(fileLogDTO);

            return new EncryptionDTO(finalZipName, response.getPassword());

        } catch (IOException e) {
            fileLogDTO = new FileLogDTO(null, originalFileName, null, null, "ENCRYPTION", localTime, "FAILURE", "Failure due to I/O error: " + e.getMessage());
            fileLogService.saveLog(fileLogDTO);
            log.error("Encryption failed due to I/O error for file: {}", originalFileName, e);
            throw new RuntimeException("Encryption failed due to I/O error ", e);
        } catch (Exception e) {
            fileLogDTO = new FileLogDTO(null, originalFileName, null, null, "ENCRYPTION", localTime, "FAILURE", "Failed due to Unexpected Error: " + e.getMessage());
            fileLogService.saveLog(fileLogDTO);
            log.error("Encryption failed due to unexpected error for file: {}", originalFileName, e);
            throw new RuntimeException("Encryption failed due to unexpected error ", e);
        } finally {
            // CRITICAL: Ensure all temporary files are deleted
            try {
                if (tempOriginalFilePath != null) Files.deleteIfExists(tempOriginalFilePath);
                if (tempEncryptedFilePath != null) Files.deleteIfExists(tempEncryptedFilePath);
                if (tempQrFilePath != null) Files.deleteIfExists(tempQrFilePath);
                if (zipPath != null) Files.deleteIfExists(zipPath); // Delete the final zip too after email/return
                log.info("Temporary files cleaned up after encryption for: {}", originalFileName);
            } catch (IOException e) {
                log.warn("Failed to clean up temporary files after encryption for {}: {}", originalFileName, e.getMessage());
            }
        }
    }

    @Override
    public DecryptionDTO handleDecryption(MultipartFile multipartFile, String password) throws Exception {
        String encryptedFileName = multipartFile.getOriginalFilename();
        // --- START FIX for original_file_name NOT NULL constraint ---
        if (encryptedFileName == null || encryptedFileName.trim().isEmpty()) {
            encryptedFileName = "unnamed_file_" + System.currentTimeMillis(); // Provide a default name
            log.warn("MultipartFile.getOriginalFilename() returned null or empty. Using default name: {}", encryptedFileName);
        }
        // --- END FIX ---
        log.info("Decryption request for file: {}", encryptedFileName);
        Path tempDir = Files.createTempDirectory("decrypt"); // Unique temporary directory
        Path zipPath = tempDir.resolve(encryptedFileName); // Copy uploaded zip to tempDir
        Files.copy(multipartFile.getInputStream(), zipPath, StandardCopyOption.REPLACE_EXISTING);
        ZoneId zone = ZoneId.of("Asia/Kolkata");
        LocalDateTime localTime = LocalDateTime.now(zone);

        FileLogDTO fileLogDTO = null; // Declare outside try for finally block access

        try {
            File encryptedTxt = unzipAndExtract(zipPath.toFile(), tempDir); // Extracts to tempDir
            String extractedEncryptedFileName = encryptedTxt.getName();
            File qrFile = tempDir.resolve("qr.png").toFile();

            if (!encryptedTxt.exists() || !qrFile.exists()) {
                throw new RuntimeException("ZIP doesn't contain required files (encrypted .txt and qr.png)");
            }

            String decodedKey = QRCodeUtil.extractKeyFromQRCode(qrFile);
            log.debug("Extracted QR key: {}", decodedKey); // Debug level

            PythonDecryptResponse response = callFlaskDecryptAPI(encryptedTxt, decodedKey, password);
            log.debug("Output File from API (truncated for logging): {}", response.getOutputFile() != null ? response.getOutputFile().length + " bytes" : "null");

            if (response.getExitCode() != 0) {
                fileLogDTO = new FileLogDTO(null, encryptedFileName, null, null, "DECRYPTION", localTime, "FAILURE", response.getStderr());
                fileLogService.saveLog(fileLogDTO);
                throw new RuntimeException("Decryption Failed: " + response.getStderr());
            }

            String decryptedFileName = null;
            if (extractedEncryptedFileName != null) {
                decryptedFileName = extractedEncryptedFileName.replace("_encrypted.txt", "_decrypted.txt");
                Path decryptedPath = Paths.get(decryptedDir, decryptedFileName); // This is in a "decryptedDir"
                Files.write(decryptedPath, response.getOutputFile());
            }

            fileLogDTO = new FileLogDTO(null, encryptedFileName, null, decryptedFileName, "DECRYPTION", localTime, "SUCCESS", response.getStdout());
            fileLogService.saveLog(fileLogDTO);

            return new DecryptionDTO(decryptedFileName); // Returns the name, client needs to fetch data

        } catch (IOException e) {
            fileLogDTO = new FileLogDTO(null, encryptedFileName, null, null, "DECRYPTION", localTime, "FAILURE", "I/O error: " + e.getMessage());
            fileLogService.saveLog(fileLogDTO);
            log.error("Decryption Failed due to I/O error for file: {}", encryptedFileName, e);
            throw new RuntimeException("Decryption Failed due to I/O error", e);
        } catch (Exception e) {
            fileLogDTO = new FileLogDTO(null, encryptedFileName, null, null, "DECRYPTION", localTime, "FAILURE", "Unexpected error: " + e.getMessage());
            fileLogService.saveLog(fileLogDTO);
            log.error("Decryption Failed due to Unexpected error for file: {}", encryptedFileName, e);
            throw new RuntimeException("Decryption Failed due to Unexpected error", e);
        } finally {
            // CRITICAL: Ensure all temporary files and directories are deleted
            try {
                if (tempDir != null && Files.exists(tempDir)) {
                    Files.walk(tempDir)
                            .sorted(java.util.Comparator.reverseOrder())
                            .map(Path::toFile)
                            .forEach(File::delete);
                    Files.deleteIfExists(tempDir); // Delete the temp directory itself
                }
                log.info("Temporary directory cleaned up after decryption for: {}", encryptedFileName);
            } catch (IOException e) {
                log.warn("Failed to clean up temporary directory after decryption for {}: {}", encryptedFileName, e.getMessage());
                // Log this, but don't rethrow as the main operation might have succeeded.
            }
        }
    }

    private PythonDecryptResponse callFlaskDecryptAPI(File encryptedTxt, String keyHex, String password) {
        String url = "http://localhost:5000/decrypt"; // Consider making this configurable

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            byte[] fileBytes = Files.readAllBytes(encryptedTxt.toPath());
            String base64File = Base64.getEncoder().encodeToString(fileBytes);

            Map<String, Object> body = new HashMap<>();
            log.debug("Flask Decrypt API call - Key: {}, Password: {}", keyHex, password); // Debug for sensitive info
            body.put("key", keyHex);
            body.put("password", password);
            body.put("encrypted_file", base64File);

            RequestEntity<Map<String, Object>> requestEntity = new RequestEntity<>(body, headers, HttpMethod.POST, URI.create(url));
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(requestEntity, new ParameterizedTypeReference<Map<String, Object>>() {});

            Map<String, Object> responseBody = response.getBody();

            if (responseBody != null && responseBody.containsKey("error") && responseBody.get("error").equals("Incorrect Password")) {
                return new PythonDecryptResponse("", "Incorrect password provided.", 401, null);
            }

            if (responseBody == null || !responseBody.containsKey("original_file")) {
                return new PythonDecryptResponse("", "Missing original file in Flask response.", 500, null);
            }

            String base64DecryptedFile = (String) responseBody.get("original_file");
            byte[] originalBytes = Base64.getDecoder().decode(base64DecryptedFile);

            return new PythonDecryptResponse("Success", "", 0, originalBytes);

        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            log.error("Flask Decryption API HTTP error: Status {} Body: {}", ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
            return new PythonDecryptResponse("", ex.getResponseBodyAsString(), ex.getStatusCode().value(), null);
        } catch (Exception e) {
            log.error("Error calling Flask Decryption API", e); // Use logger
            return new PythonDecryptResponse("", "Failed to call Flask decrypt service: " + e.getMessage(), 500, null);
        }
    }

    private PythonEncryptResponse callFlaskEncryptAPI(File file) {
        String url = "http://localhost:5000/encrypt"; // Consider making this configurable

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            byte[] fileBytes = Files.readAllBytes(file.toPath());
            String base64File = Base64.getEncoder().encodeToString(fileBytes);

            Map<String, Object> body = new HashMap<>();
            body.put("encrypted_file", base64File); // Assuming Flask expects "original_file" for encryption

            RequestEntity<Map<String, Object>> requestEntity = new RequestEntity<>(body, headers, HttpMethod.POST, URI.create(url));
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(requestEntity, new ParameterizedTypeReference<Map<String, Object>>() {});

            Map<String, Object> bodyMap = response.getBody();

            if (bodyMap == null || !bodyMap.containsKey("encrypted") || !bodyMap.containsKey("password") || !bodyMap.containsKey("qr")) {
                log.warn("Invalid response from Flask encryption service: Missing expected keys (encrypted, password, qr). Response: {}", bodyMap);
                return new PythonEncryptResponse("", "Invalid response from encryption service", 500, null, null, null);
            }

            String encryptedBase64 = (String) bodyMap.get("encrypted");
            String qrBase64 = (String) bodyMap.get("qr");
            String password = (String) bodyMap.get("password");

            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedBase64);
            byte[] qrBytes = Base64.getDecoder().decode(qrBase64);

            return new PythonEncryptResponse("Encryption Successful", "", 0, encryptedBytes, qrBytes, password);

        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            log.error("Flask Encryption API HTTP error: Status {} Body: {}", ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
            return new PythonEncryptResponse("", ex.getResponseBodyAsString(), ex.getStatusCode().value(), null, null, null);
        } catch (Exception e) {
            log.error("Error calling Flask Encryption API", e);
            return new PythonEncryptResponse("", "Failed to call Flask encrypt service: " + e.getMessage(), 500, null, null, null);
        }
    }

    // Helper method to unzip and extract specific files
    private File unzipAndExtract(File zipFile, Path tempDir) throws IOException {
        File encryptedFile = null;
        File qrFile = null;

        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(zipFile.toPath()))) {
            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                String entryName = zipEntry.getName();
                // Ensure paths are safe and within tempDir
                Path outputPath = tempDir.resolve(entryName).normalize();
                if (!outputPath.startsWith(tempDir)) {
                    throw new IOException("Attempted Zip Slip attack: " + entryName);
                }

                if (!zipEntry.isDirectory()) {
                    if (entryName.endsWith("_encrypted.txt")) {
                        encryptedFile = outputPath.toFile();
                        Files.copy(zis, outputPath, StandardCopyOption.REPLACE_EXISTING);
                    } else if (entryName.equals("qr.png")) {
                        qrFile = outputPath.toFile();
                        Files.copy(zis, outputPath, StandardCopyOption.REPLACE_EXISTING);
                    }
                }
                zis.closeEntry();
            }
        }

        if (encryptedFile == null || qrFile == null) {
            throw new RuntimeException("ZIP doesn't contain required files (qr.png and a file ending with _encrypted.txt)");
        }

        return encryptedFile;
    }

    private void addToZipFile(Path filePath, ZipOutputStream zos) throws IOException {
        try (InputStream fis = Files.newInputStream(filePath)) {
            ZipEntry zipEntry = new ZipEntry(filePath.getFileName().toString());
            zos.putNextEntry(zipEntry);

            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) >= 0) {
                zos.write(buffer, 0, len);
            }
            zos.closeEntry();
        }
    }

    // These internal classes are fine, but can be moved to separate files if they grow complex.
    // Consider using Lombok's @Data, @Builder, @AllArgsConstructor for conciseness if desired.
    private static class PythonDecryptResponse {
        private final String stdout;
        private final String stderr;
        private final int exitCode;
        private final byte[] outputFile;

        public PythonDecryptResponse(String stdout, String stderr, int exitCode, byte[] outputFile) {
            this.stderr = stderr;
            this.exitCode = exitCode;
            this.stdout = stdout;
            this.outputFile = outputFile;
        }

        public String getStdout() { return stdout; }
        public String getStderr() { return stderr; }
        public int getExitCode() { return exitCode; }
        public byte[] getOutputFile() { return outputFile; }
    }

    private static class PythonEncryptResponse {
        private final String stdout;
        private final String stderr;
        private final int exitCode;
        private final byte[] encryptedFile;
        private final byte[] qrCodeFile;
        private final String password;

        public PythonEncryptResponse(String stdout, String stderr, int exitCode, byte[] encryptedFile, byte[] qrCodeFile, String password) {
            this.stderr = stderr;
            this.exitCode = exitCode;
            this.stdout = stdout;
            this.encryptedFile = encryptedFile;
            this.qrCodeFile = qrCodeFile;
            this.password = password;
        }

        public String getStdout() { return stdout; }
        public String getStderr() { return stderr; }
        public int getExitCode() { return exitCode; }
        public byte[] getEncryptedFile() { return encryptedFile; }
        public byte[] getQrCodeFile() { return qrCodeFile; }
        public String getPassword() { return password; }
    }
}