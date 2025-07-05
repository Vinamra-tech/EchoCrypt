package com.vinamra.encrypt_decrypt_backend.service;

import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.vinamra.encrypt_decrypt_backend.dto.DecryptionDTO;
import com.vinamra.encrypt_decrypt_backend.dto.EncryptionDTO;

public interface EncrytionService {

    public EncryptionDTO handleEncryption(MultipartFile multipartFile, Optional<String> userMail) throws Exception;

    public DecryptionDTO handleDecryption(MultipartFile multipartFile,String password) throws Exception;

}
