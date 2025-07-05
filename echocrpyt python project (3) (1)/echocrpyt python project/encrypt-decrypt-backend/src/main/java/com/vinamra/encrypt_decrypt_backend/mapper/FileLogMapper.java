package com.vinamra.encrypt_decrypt_backend.mapper;

import com.vinamra.encrypt_decrypt_backend.dto.FileLogDTO;
import com.vinamra.encrypt_decrypt_backend.entity.FileLog;

public class FileLogMapper {

    public static FileLogDTO mapToFileLogDTO(FileLog log) {
        return FileLogDTO.builder()
                .id(log.getId())
                .originalFileName(log.getOriginalFileName())
                .encryptedFileName(log.getEncryptedFileName())
                .decryptedFileName(log.getDecryptedFileName())
                .operationType(log.getOperationType().name())
                .operationTime(log.getOperationTime())
                .status(log.getStatus())
                .remarks(log.getRemarks())
                .build();
    }

    public static FileLog mapToFileLog(FileLogDTO dto) {
        FileLog.FileLogBuilder builder = FileLog.builder()
                .id(dto.getId())
                .originalFileName(dto.getOriginalFileName())
                .encryptedFileName(dto.getEncryptedFileName())
                .decryptedFileName(dto.getDecryptedFileName())
                .operationType(FileLog.OperationType.valueOf(dto.getOperationType()))
                .operationTime(dto.getOperationTime())
                .status(dto.getStatus())
                .remarks(dto.getRemarks());

        return builder.build();
    }
}
