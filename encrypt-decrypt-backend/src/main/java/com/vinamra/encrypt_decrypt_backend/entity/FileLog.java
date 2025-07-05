package com.vinamra.encrypt_decrypt_backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "file_log")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "original_file_name")
    private String originalFileName;

    @Column(name = "encrypted_file_name")
    private String encryptedFileName;

    @Column(name = "decrypted_file_name")
    private String decryptedFileName;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation_type", nullable = false)
    private OperationType operationType;

    @Column(name = "operation_time", nullable = false)
    private LocalDateTime operationTime;


    @Column(name = "status")
    private String status;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    @PrePersist
    public void onPrePersist() {
        if (operationTime == null)
            operationTime = LocalDateTime.now();

        if (status == null || status.isBlank())
            status = "PENDING";
    }

    @PreUpdate
    public void onPreUpdate() {
        operationTime = LocalDateTime.now();
    }

    public enum OperationType {
        ENCRYPTION,
        DECRYPTION
    }
}
