package com.vinamra.encrypt_decrypt_backend.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vinamra.encrypt_decrypt_backend.entity.FileLog;

public interface FileLogRepository extends JpaRepository<FileLog ,Long> {

    List<FileLog> findByStatus(String status);
    List <FileLog> findByOperationType(FileLog.OperationType operationType);
    List <FileLog> findByOperationTimeBetween(LocalDateTime start , LocalDateTime end);
    

}
