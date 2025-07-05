package com.vinamra.encrypt_decrypt_backend.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional; 

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.vinamra.encrypt_decrypt_backend.dto.DecryptionDTO;
import com.vinamra.encrypt_decrypt_backend.dto.EncryptionDTO;
import com.vinamra.encrypt_decrypt_backend.service.EncrytionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@CrossOrigin("*")
@Slf4j
public class UserController {

    public final EncrytionService encrytionService;

    @PostMapping("/encrypt")
    public ResponseEntity<?> encryptFile(
        @RequestParam("file") MultipartFile file,
        @RequestParam(value = "email", required = false) Optional<String> email 
        // If you send a "consent" parameter from Streamlit, you could add it here too:
        // @RequestParam(value = "consentGiven", defaultValue = "false")
    ) {
        try {
            
            log.info("Received encryption request for file: {} from email (optional): {}", file.getOriginalFilename(), email.orElse("N/A"));

            // Pass the Optional email and consent (if you add it) to the service
            EncryptionDTO result = encrytionService.handleEncryption(file, email);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Encryption failed for file: {}. Error: {}", file.getOriginalFilename(), e.getMessage(), e); // Log full stack trace
            Map<String, String> error = new HashMap<>();
            error.put("error", "Encryption failed. Please try again or contact support."); // Generic message to client
            return ResponseEntity.status(500).body(error);
        }
    }

    @PostMapping("/decrypt")
    public ResponseEntity<?> decryptFile(
        @RequestParam("file") MultipartFile file,
        @RequestParam("password") String password
    ) {
        
        log.info("Received decryption request for file: {}", file.getOriginalFilename());

        try {
            DecryptionDTO result = encrytionService.handleDecryption(file, password);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Decryption failed for file: {}. Error: {}", file.getOriginalFilename(), e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Decryption failed. Please check your password and try again."); 
            return ResponseEntity.status(500).body(error);
        }
    }
}