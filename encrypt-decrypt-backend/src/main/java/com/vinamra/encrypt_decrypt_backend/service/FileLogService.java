package com.vinamra.encrypt_decrypt_backend.service;

import java.time.LocalDateTime;
import java.util.List;

import com.vinamra.encrypt_decrypt_backend.dto.FileLogDTO;

public interface FileLogService {

    public FileLogDTO saveLog(FileLogDTO dto);

    public List<FileLogDTO> getAllLogs();

    public FileLogDTO getLogById(Long id);

    public List<FileLogDTO> getLogsByStatus(String status);

    public List<FileLogDTO> getLogsByOperation(String operationType);

    public List<FileLogDTO> getLogsByOperationTimeBetween(LocalDateTime start ,LocalDateTime end);


}
