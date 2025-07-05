package com.vinamra.encrypt_decrypt_backend.controller;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vinamra.encrypt_decrypt_backend.dto.FileLogDTO;
import com.vinamra.encrypt_decrypt_backend.service.FileLogService;

import lombok.RequiredArgsConstructor;
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor

public class AdminController {

    private final FileLogService fileLogService;

    @GetMapping("/logs/all")
    public ResponseEntity<List<FileLogDTO>> getAllLogs(){
        
        return ResponseEntity.ok(fileLogService.getAllLogs());
    }
    
    @GetMapping("/logs/status")
    public ResponseEntity<List<FileLogDTO>> getLogsByStatus(@RequestParam("status") String status){

        return ResponseEntity.ok(fileLogService.getLogsByStatus(status));
    }
    
    @GetMapping("/logs/type")
    public ResponseEntity<List<FileLogDTO>> getLogsByType(@RequestParam("type") String type ){

        return ResponseEntity.ok(fileLogService.getLogsByOperation(type));
    }

    @GetMapping("/log/id")
    public ResponseEntity<FileLogDTO> getLogById(@RequestParam("id") Long id ){
         
        return ResponseEntity.ok(fileLogService.getLogById(id));
    }

   @GetMapping("/logs/range")
public ResponseEntity<List<FileLogDTO>> getLogsByRange(
    @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
    @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
) {
    
    ZoneId zone = ZoneId.of("Asia/Kolkata");

    ZonedDateTime startZoned = start.atZone(zone);
    ZonedDateTime endZoned = end.atZone(zone);

    LocalDateTime startTime = startZoned.toLocalDateTime();
    LocalDateTime endTime = endZoned.toLocalDateTime();

    return ResponseEntity.ok(fileLogService.getLogsByOperationTimeBetween(startTime, endTime));
}


}
