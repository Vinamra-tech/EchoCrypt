package com.vinamra.encrypt_decrypt_backend.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileLogDTO {

    private Long id;
    private String originalFileName;
    private String encryptedFileName;
    private String decryptedFileName;
    private String operationType;
    private LocalDateTime operationTime;
    private String status;
    private String remarks;  
}
