
package com.vinamra.encrypt_decrypt_backend.service.impl;


import java.time.LocalDateTime;

import java.util.List;
import java.util.stream.Collectors;


import org.springframework.stereotype.Service;

import com.vinamra.encrypt_decrypt_backend.dto.FileLogDTO;
import com.vinamra.encrypt_decrypt_backend.entity.FileLog;
import com.vinamra.encrypt_decrypt_backend.mapper.FileLogMapper;
import com.vinamra.encrypt_decrypt_backend.repository.FileLogRepository;
import com.vinamra.encrypt_decrypt_backend.service.FileLogService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class FileLogServiceImpl implements FileLogService {

    private final FileLogRepository fileLogRepository;

    @Override
    public FileLogDTO saveLog(FileLogDTO dto) {

        FileLog fileLog = FileLogMapper.mapToFileLog(dto);
        FileLog saved =fileLogRepository.save(fileLog);
        return FileLogMapper.mapToFileLogDTO(saved);
    }

    @Override
    public List<FileLogDTO> getAllLogs() {
        
        List<FileLog> list = fileLogRepository.findAll();
        return list.stream().map(fileLog->FileLogMapper.mapToFileLogDTO(fileLog)).collect(Collectors.toList());
    }

    @Override
    public FileLogDTO getLogById(Long id) {

        FileLog fileLog = fileLogRepository.findById(id).orElseThrow(()-> new RuntimeException("File Log doesn't exist with id :"+id));
        return FileLogMapper.mapToFileLogDTO(fileLog);
    }

    @Override
    public List<FileLogDTO> getLogsByStatus(String status) {

        List<FileLog> list =fileLogRepository.findByStatus(status);
        return list.stream().map(fileLog->FileLogMapper.mapToFileLogDTO(fileLog)).collect(Collectors.toList());
    }

    @Override
    public List<FileLogDTO> getLogsByOperation(String operationType) {

        FileLog.OperationType type;
        try {
            type= FileLog.OperationType.valueOf(operationType.toUpperCase()); 
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid Operation Type"); 
        }
        List<FileLog> list = fileLogRepository.findByOperationType(type);
        return list.stream().map(fileLog->FileLogMapper.mapToFileLogDTO(fileLog)).collect(Collectors.toList());
    }

    @Override
    public List<FileLogDTO> getLogsByOperationTimeBetween(LocalDateTime start, LocalDateTime end) {
        
        List<FileLog> list = fileLogRepository.findByOperationTimeBetween(start,end);
        return list.stream().map(fileLog->FileLogMapper.mapToFileLogDTO(fileLog)).collect(Collectors.toList());
    }
}